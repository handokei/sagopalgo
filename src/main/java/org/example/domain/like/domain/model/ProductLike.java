package org.example.domain.like.domain.model;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.example.global.config.entity.BaseEntity;

@Getter
@Entity
@Table(name = "products_likes",
uniqueConstraints = {
        @UniqueConstraint(columnNames = {"user_id", "product_id"})
})
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class ProductLike extends BaseEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id")
    private Long userId;

    @Column(name = "product_id")
    private Long productId;

    protected ProductLike(Long userId, Long productId) {
        this.userId = userId;
        this.productId = productId;
    }

    public static ProductLike of(Long userId, Long productId) {
        return new ProductLike(userId,productId);
    }

}
