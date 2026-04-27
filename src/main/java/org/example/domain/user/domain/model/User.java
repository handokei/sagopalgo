package org.example.domain.user.domain.model;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.example.domain.user.exception.UserErrorCode;
import org.example.domain.user.exception.UserException;
import org.example.global.config.entity.BaseEntity;

@Entity
@Getter
@Table(name = "users", indexes = {
        @Index(name = "idx_user_email", columnList = "email"),
        @Index(name = "idx_user_is_deleted", columnList = "isDeleted")
})
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class User extends BaseEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String email;

    @Column
    private String password;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String nickname;

    @Enumerated(EnumType.STRING)
    private UserRole userRole;

    @Column(length = 20)
    private String provider;

    @Column(length = 100)
    private String providerId;

    @Column(length = 20)
    private String phoneNumber;

    private boolean isDeleted = false;


    private User(String email, String password, String name, String nickname, UserRole userRole, String phoneNumber) {
        if (email == null || email.isBlank())        throw new UserException(UserErrorCode.INVALID_EMAIL);
        if (password == null || password.isBlank())  throw new UserException(UserErrorCode.INVALID_PASSWORD);
        if (name == null || name.isBlank())          throw new UserException(UserErrorCode.INVALID_NAME);
        if (nickname == null || nickname.isBlank())  throw new UserException(UserErrorCode.INVALID_NICKNAME);
        if (userRole == null)                        throw new UserException(UserErrorCode.INVALID_ROLE);
        this.email = email;
        this.password = password;
        this.name = name;
        this.nickname = nickname;
        this.userRole = userRole;
        this.phoneNumber = phoneNumber;
    }

    private User(String email, String name, String nickname, UserRole userRole,
                 String provider, String providerId) {
        if (email == null || email.isBlank())    throw new UserException(UserErrorCode.INVALID_EMAIL);
        if (name == null || name.isBlank())      throw new UserException(UserErrorCode.INVALID_NAME);
        if (nickname == null || nickname.isBlank()) throw new UserException(UserErrorCode.INVALID_NICKNAME);
        if (provider == null || provider.isBlank() || providerId == null || providerId.isBlank())
            throw new UserException(UserErrorCode.INVALID_PROVIDER);
        this.email = email;
        this.password = null;
        this.name = name;
        this.nickname = nickname;
        this.userRole = userRole;
        this.provider = provider;
        this.providerId = providerId;
    }

    public static User of(String email, String password, String name, String nickname, UserRole userRole, String phoneNumber) {
        return new User(email, password, name, nickname, userRole, phoneNumber);
    }

    public static User ofOAuth2(String email, String name, String nickname,
                                String provider, String providerId) {
        return new User(email, name, nickname, UserRole.ROLE_USER, provider, providerId);
    }

    // SMS 인증·전화번호 없이 시스템이 직접 생성하는 관리자 계정 전용
    public static User ofAdmin(String email, String password, String name, String nickname) {
        return new User(email, password, name, nickname, UserRole.ROLE_ADMIN, null);
    }

    // 회원 정보 수정
    public void update(String password, String nickname) {
        this.password = password;
        this.nickname = nickname;
    }

    // OAuth2 재로그인 시 이름/닉네임 갱신
    public void updateOAuth2Info(String name, String nickname) {
        this.name = name;
        this.nickname = nickname;
    }
}
