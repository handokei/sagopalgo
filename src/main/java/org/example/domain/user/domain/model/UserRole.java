package org.example.domain.user.domain.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum UserRole {

    ROLE_ADMIN("관리자"),
    ROLE_USER("사용자");

    private final String userRole;
}
