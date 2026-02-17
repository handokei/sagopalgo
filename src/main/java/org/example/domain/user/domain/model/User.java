package org.example.domain.user.domain.model;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.example.global.config.entity.BaseEntity;

@Entity
@Getter
@Table(name = "users")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class User extends BaseEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String nickname;

    @Enumerated(EnumType.STRING)
    private UserRole userRole;

    private boolean isDeleted = false;


    private User(String email, String password, String name, String nickname, UserRole userRole) {
        this.email = email;
        this.password = password;
        this.name = name;
        this.nickname = nickname;
        this.userRole = userRole;
    }


    public static User of(String email, String password, String name, String nickname, UserRole userRole){

        return new User(email,password,name,nickname, userRole);
    }

    //회원 정보 수정
    public void update(String password, String nickname) {
        this.password = password;
        this.nickname = nickname;
    }
}
