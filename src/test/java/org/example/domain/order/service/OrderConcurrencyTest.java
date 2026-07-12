package org.example.domain.order.service;

import org.example.domain.category.domain.model.Category;
import org.example.domain.category.domain.repository.CategoryRepository;
import org.example.domain.order.controller.dto.OrderCreateRequestDto;
import org.example.domain.order.controller.dto.OrderItemRequestDto;
import org.example.domain.product.domain.model.Product;
import org.example.domain.product.domain.model.ProductStatus;
import org.example.domain.product.domain.repository.ProductRepository;
import org.example.domain.user.domain.model.User;
import org.example.domain.user.domain.model.UserRole;
import org.example.domain.user.domain.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.ReentrantLock;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * "재고 10개 상품에 50명이 동시 주문해도 정확히 10명만 성공한다"를 실제 코드로 증명한다.
 *
 * <p>실제 {@link OrderFacade}(락 담당) → {@link OrderTransactionService}(트랜잭션) →
 * {@code StockService} → H2 DB + {@code Product.@Version}까지 그대로 실행한다.
 * 오직 {@link RedissonClient}만 in-JVM 락(ReentrantLock)으로 대체한다 —
 * 단일 프로세스 테스트에서는 Redisson의 분산 상호배제와 동일한 보증을 제공하므로,
 * facade의 "락 획득 → 트랜잭션 커밋 → 락 해제" 순서가 정확히 10개 커밋을 만드는지 검증된다.
 * (여러 인스턴스 간 분산성은 Redisson의 몫으로, Redis 없이는 검증 범위 밖.)
 */
@SpringBootTest(properties = "spring.kafka.listener.auto-startup=false")
@ActiveProfiles("test")
class OrderConcurrencyTest {

    @Autowired
    private OrderFacade orderFacade;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private CategoryRepository categoryRepository;
    @Autowired
    private ProductRepository productRepository;

    // 실제 RedissonClient는 startup 시 Redis에 연결하므로, 빈 정의를 mock으로 대체한다.
    @MockBean
    private RedissonClient redissonClient;

    // 알림(Kafka) 발행 경로는 브로커 없이 no-op 처리
    @MockBean
    private KafkaTemplate<String, Object> kafkaTemplate;

    @BeforeEach
    void setUpInJvmLock() {
        // productId별 ReentrantLock으로 뒷받침 — 단일 프로세스에서 분산락과 동일한 상호배제
        Map<String, ReentrantLock> backing = new ConcurrentHashMap<>();
        when(redissonClient.getLock(anyString())).thenAnswer(inv -> {
            String key = inv.getArgument(0);
            ReentrantLock real = backing.computeIfAbsent(key, k -> new ReentrantLock());

            RLock rlock = mock(RLock.class);
            when(rlock.getName()).thenReturn(key);
            // tryLock(waitTime, leaseTime, unit) → 대기시간/단위만 사용 (정상 타이밍 모델)
            when(rlock.tryLock(anyLong(), anyLong(), any(TimeUnit.class)))
                    .thenAnswer(i -> real.tryLock(i.getArgument(0), i.getArgument(2)));
            when(rlock.isHeldByCurrentThread()).thenAnswer(i -> real.isHeldByCurrentThread());
            doAnswer(i -> {
                real.unlock();
                return null;
            }).when(rlock).unlock();
            return rlock;
        });
    }

    @Test
    @DisplayName("재고 10개 상품에 50명이 동시 주문하면 정확히 10명만 성공하고 재고는 0이 된다")
    void 재고_10개_50명_동시주문_정확히_10명만_성공() throws InterruptedException {
        // given
        int stock = 10;
        int concurrentUsers = 50;

        User seller = userRepository.save(User.of(
                "seller-concurrency@test.com", "pw", "판매자", "seller", UserRole.ROLE_USER, "01000000001"));
        User buyer = userRepository.save(User.of(
                "buyer-concurrency@test.com", "pw", "구매자", "buyer", UserRole.ROLE_USER, "01000000002"));
        Category category = categoryRepository.save(Category.of("동시성테스트", null));
        Product product = productRepository.save(Product.of(
                seller, "한정 수량 상품", "재고 10개 동시성 검증용", 10_000, stock, ProductStatus.ON_SALE, category));

        Long productId = product.getId();
        Long buyerId = buyer.getId();

        ExecutorService pool = Executors.newFixedThreadPool(concurrentUsers);
        CountDownLatch ready = new CountDownLatch(concurrentUsers);
        CountDownLatch startGun = new CountDownLatch(1);
        AtomicInteger success = new AtomicInteger();
        AtomicInteger failure = new AtomicInteger();

        // when: 50명이 동시에 같은 상품 1개씩 주문
        for (int i = 0; i < concurrentUsers; i++) {
            pool.submit(() -> {
                ready.countDown();
                try {
                    startGun.await();
                    OrderCreateRequestDto request = new OrderCreateRequestDto(
                            List.of(new OrderItemRequestDto(productId, 1)),
                            "수령인", "01000000000", "서울시 어딘가");
                    orderFacade.createOrder(buyerId, request);
                    success.incrementAndGet();
                } catch (Exception e) {
                    failure.incrementAndGet();
                }
            });
        }
        ready.await();
        startGun.countDown(); // 동시 출발
        pool.shutdown();
        boolean finished = pool.awaitTermination(60, TimeUnit.SECONDS);

        // then
        assertThat(finished).isTrue();
        assertThat(success.get()).isEqualTo(stock);            // 정확히 10명 성공
        assertThat(failure.get()).isEqualTo(concurrentUsers - stock); // 40명 실패
        Product after = productRepository.findById(productId).orElseThrow();
        assertThat(after.getStock()).isZero();                 // 재고 정확히 0 (초과 판매 없음)
    }
}
