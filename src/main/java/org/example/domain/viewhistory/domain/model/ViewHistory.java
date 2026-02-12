package org.example.domain.viewhistory.domain.model;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.example.domain.product.domain.model.ProductCategory;
import org.example.global.config.entity.BaseEntity;

@Entity
@Table(name = "view_histories")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class ViewHistory extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long userId;

    private Long productId;

    @Enumerated(EnumType.STRING)
    private ProductCategory productCategory;

    private ViewHistory(Long userId, Long productId, ProductCategory productCategory) {
        this.userId = userId;
        this.productId = productId;
        this.productCategory = productCategory;
    }

    public static ViewHistory of(Long userId, Long productId, ProductCategory productCategory) {
        return new ViewHistory(userId, productId, productCategory);
    }
}
