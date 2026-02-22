package org.example.global.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.domain.category.domain.model.Category;
import org.example.domain.category.domain.repository.CategoryRepository;
import org.example.domain.user.domain.model.User;
import org.example.domain.user.domain.model.UserRole;
import org.example.domain.user.domain.repository.UserRepository;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class InitDB implements ApplicationRunner {

    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public void run(ApplicationArguments args) {
        initAdmin();
        initCategories();
    }

    private void initAdmin() {
        String adminEmail = "admin@sagopalgo.com";

        if (userRepository.existsByEmailAndIsDeletedFalse(adminEmail)) {
            log.info("관리자 계정이 이미 존재합니다: {}", adminEmail);
            return;
        }

        User admin = User.of(
                adminEmail,
                passwordEncoder.encode("admin1234!"),
                "관리자",
                "관리자",
                UserRole.ROLE_ADMIN
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
}
