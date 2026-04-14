package org.example.global.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.domain.category.domain.model.Category;
import org.example.domain.category.domain.repository.CategoryRepository;
import org.example.domain.product.domain.model.Product;
import org.example.domain.product.domain.model.ProductStatus;
import org.example.domain.product.domain.repository.ProductRepository;
import org.example.domain.user.domain.model.User;
import org.example.domain.user.domain.repository.UserRepository;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class InitDB implements ApplicationRunner {

    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;
    private final ProductRepository productRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public void run(ApplicationArguments args) {
        initAdmin();
        initCategories();
        initSampleProducts();
    }

    private void initAdmin() {
        String adminEmail    = System.getenv().getOrDefault("INIT_ADMIN_EMAIL", "admin@sagopalgo.com");
        String adminPassword = System.getenv().getOrDefault("INIT_ADMIN_PASSWORD", "Admin1234!");

        if (userRepository.existsByEmailAndIsDeletedFalse(adminEmail)) {
            log.info("관리자 계정이 이미 존재합니다: {}", adminEmail);
            return;
        }

        User admin = User.ofAdmin(
                adminEmail,
                passwordEncoder.encode(adminPassword),
                "관리자",
                "관리자"
        );
        userRepository.save(admin);
        log.info("관리자 계정 생성 완료: {}", adminEmail);
    }

    private void initCategories() {
        List<String> defaultCategories = List.of(
                "티셔츠",
                "후드",
                "아우터",
                "바지",
                "신발"
        );

        for (String name : defaultCategories) {
            if (categoryRepository.existsByNameAndIsDeletedFalse(name)) {
                log.info("카테고리가 이미 존재합니다: {}", name);
                continue;
            }
            Category category = Category.of(name, null);
            categoryRepository.save(category);
            log.info("카테고리 생성 완료: {}", name);
        }
    }

    private void initSampleProducts() {
        if (productRepository.count() > 0) {
            log.info("샘플 상품이 이미 존재합니다.");
            return;
        }

        String adminEmail = System.getenv().getOrDefault("INIT_ADMIN_EMAIL", "admin@sagopalgo.com");
        User seller = userRepository.findByEmailAndIsDeletedFalse(adminEmail)
                .orElseThrow(() -> new RuntimeException("관리자 계정 없음"));

        // [카테고리명, 상품명, 설명, 가격, 재고]
        List<Object[]> sampleData = List.of(
                new Object[]{"티셔츠", "나이키 드라이핏 반팔 티셔츠", "나이키 드라이핏 소재 반팔 티셔츠. 스포츠 활동 시 땀 흡수가 빠르고 통기성이 뛰어납니다. 블랙 M 사이즈, 착용 3회 미만.", 25000, 3},
                new Object[]{"티셔츠", "유니클로 에어리즘 라운드넥 티", "유니클로 에어리즘 소재 라운드넥 반팔 티셔츠. 여름철 시원하게 입기 좋고 일상복으로 활용도 높습니다. 화이트 L 사이즈, 세탁 2회.", 12000, 5},
                new Object[]{"티셔츠", "무신사 스탠다드 오버핏 반팔", "무신사 스탠다드 오버핏 반팔 티셔츠. 루즈한 핏으로 편안하게 입을 수 있습니다. 그레이 M 사이즈, 거의 새 상품.", 18000, 2},
                new Object[]{"후드", "챔피온 리버스위브 후드티", "챔피온 리버스위브 풀집 후드티. 두꺼운 기모 소재로 겨울철 따뜻하게 착용 가능. 네이비 L 사이즈, 착용감 우수.", 45000, 2},
                new Object[]{"후드", "스투시 기본 후드 스웨트셔츠", "스투시 로고 프린팅 후드 스웨트셔츠. 스트릿 캐주얼 스타일에 잘 어울리며 안감이 부드럽습니다. 블랙 M 사이즈, 세탁 3회.", 55000, 1},
                new Object[]{"후드", "아디다스 에센셜 후디", "아디다스 트레포일 로고 후드 집업. 가볍고 신축성이 좋아 운동 및 일상에서 모두 활용 가능합니다. 그레이 L 사이즈.", 38000, 4},
                new Object[]{"아우터", "노스페이스 눕시 패딩 점퍼", "노스페이스 눕시 다운 패딩. 가볍고 보온성이 뛰어나 겨울 야외 활동에 적합합니다. 블랙 M 사이즈, 구스다운 충전재, 사용감 적음.", 120000, 1},
                new Object[]{"아우터", "유니클로 울트라라이트 다운 조끼", "유니클로 울트라라이트 다운 베스트. 초경량 소재로 접어서 가방에 넣을 수 있습니다. 올리브 M 사이즈, 착용 5회 이내.", 35000, 3},
                new Object[]{"아우터", "폴로 랄프로렌 바람막이 자켓", "폴로 랄프로렌 나일론 바람막이 자켓. 봄가을 간절기에 딱 맞는 가벼운 아우터입니다. 네이비 M 사이즈, 오염 없음.", 65000, 2},
                new Object[]{"바지", "리바이스 501 청바지 인디고", "리바이스 501 오리지널 스트레이트 청바지. 클래식한 인디고 워시로 다양한 스타일에 매치하기 좋습니다. 허리 32 기장 32, 세탁 5회 이내.", 55000, 2},
                new Object[]{"바지", "나이키 조거 트레이닝 팬츠", "나이키 드라이핏 조거 팬츠. 운동할 때나 일상에서 편하게 착용 가능한 트레이닝 바지입니다. 블랙 M 사이즈, 거의 새것.", 32000, 3},
                new Object[]{"바지", "무신사 스탠다드 와이드 슬랙스", "무신사 스탠다드 와이드 핏 슬랙스. 편안한 착용감과 깔끔한 실루엣으로 출근룩에도 적합합니다. 베이지 32 사이즈.", 28000, 4},
                new Object[]{"신발", "나이키 에어맥스 90 화이트", "나이키 에어맥스 90 화이트/그레이 컬러. 쿠셔닝이 뛰어나 장시간 착용에도 발이 편합니다. 270mm, 착용 횟수 적음, 밑창 깨끗.", 75000, 1},
                new Object[]{"신발", "아디다스 스탠스미스 그린힐", "아디다스 스탠스미스 클래식 스니커즈. 어떤 옷에도 잘 어울리는 베이직한 디자인입니다. 265mm, 세탁 완료 상태 깨끗.", 60000, 2},
                new Object[]{"신발", "뉴발란스 993 메이드인USA", "뉴발란스 993 메이드인USA 모델. 프리미엄 쿠셔닝과 내구성으로 러닝 및 일상에서 모두 인기 있는 모델입니다. 275mm, 착용 3회 미만.", 150000, 1}
        );

        for (Object[] data : sampleData) {
            String categoryName = (String) data[0];
            Category category = categoryRepository.findByNameAndIsDeletedFalse(categoryName)
                    .orElseThrow(() -> new RuntimeException("카테고리 없음: " + categoryName));
            Product product = Product.of(seller, (String) data[1], (String) data[2], (int) data[3], (int) data[4], ProductStatus.ON_SALE, category);
            productRepository.save(product);
            log.info("샘플 상품 생성 완료: {}", product.getTitle());
        }
    }
}
