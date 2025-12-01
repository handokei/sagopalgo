package org.example.auth.domain.model;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.example.global.config.entity.BaseEntity;

@Entity
@Getter
@Table(name = "users")
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class User extends BaseEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(unique = false)
    private String password;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String nickname;

    private boolean isDeleted = false;


    private User(Long id, String email, String password, String name, String nickname) {
        this.id = id;
        this.email = email;
        this.password = password;
        this.name = name;
        this.nickname = nickname;
    }
}
