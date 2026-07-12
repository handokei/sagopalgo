package org.example.domain.order.service;

import lombok.RequiredArgsConstructor;
import org.example.domain.cart.domain.model.OwnerType;
import org.example.domain.cart.service.CartService;
import org.example.domain.notification.domain.model.NotificationType;
import org.example.domain.notification.event.OrderNotificationEvent;
import org.example.domain.order.controller.dto.OrderCreateRequestDto;
import org.example.domain.order.controller.dto.OrderCreateResponseDto;
import org.example.domain.order.controller.dto.OrderItemRequestDto;
import org.example.domain.order.controller.dto.OrderStatusResponseDto;
import org.example.domain.order.domain.model.Order;
import org.example.domain.order.domain.model.OrderItem;
import org.example.domain.product.service.StockService;
import org.example.domain.user.service.UserService;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * 주문 흐름의 <b>분산락 계층</b>. 트랜잭션 경계는 {@link OrderTransactionService}가 담당한다.
 *
 * <p>{@code createOrder}는 {@code @Transactional}을 두지 않는다. 락을 먼저 획득한 뒤
 * 트랜잭션 서비스를 호출하고, 그 커밋이 끝난 뒤에 락을 해제한다.
 * 즉 정상 경로에서는 <b>락 스코프가 트랜잭션 스코프를 완전히 감싸</b> 커밋 전에 락이 풀려
 * 다른 스레드가 stale 재고를 읽는 lost update를 막는다.
 *
 * <p>단, {@code leaseTime}(10초)을 명시했으므로 Redisson watchdog이 비활성이며,
 * 트랜잭션이 leaseTime을 초과하면 커밋 전에 락이 자동 만료될 수 있다. 이 경우를 대비해
 * 정합성의 최종 방어선은 {@code Product}의 {@code @Version} 낙관적 락이 담당한다
 * (동시 커밋 충돌 시 하나를 {@code OptimisticLockException}으로 실패). 분산락은 대기/직렬화,
 * 낙관적 락은 정합성 최종 보증으로 역할을 분리한다.
 */
@Component
@RequiredArgsConstructor
public class OrderFacadeImpl implements OrderFacade {

    private static final String STOCK_LOCK_PREFIX = "LOCK:stock:";
    private static final long LOCK_WAIT_SECONDS = 5L;
    private static final long LOCK_LEASE_SECONDS = 10L;

    private final OrderTransactionService orderTransactionService;
    private final OrderService orderService;
    private final UserService userService;
    private final StockService stockService;
    private final CartService cartService;
    private final ApplicationEventPublisher eventPublisher;
    private final RedissonClient redissonClient;

    @Override
    public OrderCreateResponseDto createOrder(Long userId, OrderCreateRequestDto requestDto) {
        // 데드락 방지: productId 오름차순으로 정렬해 항상 같은 순서로 락을 획득한다.
        List<RLock> locks = requestDto.getItems().stream()
                .map(OrderItemRequestDto::getProductId)
                .distinct()
                .sorted()
                .map(productId -> redissonClient.getLock(STOCK_LOCK_PREFIX + productId))
                .toList();

        List<RLock> acquired = new ArrayList<>();
        try {
            for (RLock lock : locks) {
                if (!lock.tryLock(LOCK_WAIT_SECONDS, LOCK_LEASE_SECONDS, TimeUnit.SECONDS)) {
                    throw new IllegalStateException("재고 락 획득 실패: " + lock.getName());
                }
                acquired.add(lock);
            }

            // 트랜잭션 시작 → 재고 차감/주문 생성 → 커밋(메서드 반환 시점)
            return orderTransactionService.createOrder(userId, requestDto);

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new IllegalStateException("재고 락 대기 중 인터럽트되었습니다", e);
        } finally {
            // 커밋 이후, 획득 역순으로 해제. 자신이 보유한 락만 안전하게 해제한다.
            for (int i = acquired.size() - 1; i >= 0; i--) {
                RLock lock = acquired.get(i);
                if (lock.isHeldByCurrentThread()) {
                    lock.unlock();
                }
            }
        }
    }

    @Override
    @Transactional
    public OrderStatusResponseDto cancelOrder(Long userId, Long orderId) {
        userService.validateUserExists(userId);

        OrderStatusResponseDto responseDto = orderService.cancelOrder(orderId);

        Order order = orderService.findOrder(orderId);
        for (OrderItem orderItem : order.getOrderItems()) {
            stockService.restoreStock(orderItem.getProductId(), orderItem.getQuantity());
        }

        eventPublisher.publishEvent(new OrderNotificationEvent(
                userId, NotificationType.ORDER_CANCELED,
                NotificationType.ORDER_CANCELED.getMessage() + ": " + order.getSummaryTitle(),
                order.getId()));

        return responseDto;
    }

    @Override
    @Transactional
    public OrderStatusResponseDto shipOrder(Long userId, Long orderId, String trackingNumber) {
        userService.validateUserExists(userId);

        OrderStatusResponseDto responseDto = orderService.shipOrder(orderId, trackingNumber);

        Order order = orderService.findOrder(orderId);
        eventPublisher.publishEvent(new OrderNotificationEvent(
                userId, NotificationType.ORDER_SHIPPED,
                NotificationType.ORDER_SHIPPED.getMessage() + ": " + order.getSummaryTitle(),
                order.getId()));

        return responseDto;
    }

    @Override
    @Transactional
    public OrderStatusResponseDto completeOrder(Long userId, Long orderId) {
        userService.validateUserExists(userId);

        OrderStatusResponseDto responseDto = orderService.completeOrder(orderId);

        Order order = orderService.findOrder(orderId);
        eventPublisher.publishEvent(new OrderNotificationEvent(
                userId, NotificationType.ORDER_COMPLETED,
                NotificationType.ORDER_COMPLETED.getMessage() + ": " + order.getSummaryTitle(),
                order.getId()));

        return responseDto;
    }
}
