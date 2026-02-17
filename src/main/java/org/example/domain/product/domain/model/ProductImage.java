package org.example.domain.product.domain.model;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.example.global.config.entity.BaseEntity;

@Entity
@Table(name = "product_images")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class ProductImage extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long productId;

    @Column(nullable = false)
    private String imageUrl;

    @Column(nullable = false)
    private String originalFileName;

    private int sortOrder;

    private boolean isMain = false;

    private boolean isDeleted = false;

    private ProductImage(Long productId, String imageUrl, String originalFileName, int sortOrder, boolean isMain) {
        this.productId = productId;
        this.imageUrl = imageUrl;
        this.originalFileName = originalFileName;
        this.sortOrder = sortOrder;
        this.isMain = isMain;
    }

    public static ProductImage of(Long productId, String imageUrl, String originalFileName, int sortOrder, boolean isMain) {
        return new ProductImage(productId, imageUrl, originalFileName, sortOrder, isMain);
    }

    public void setAsMain() {
        this.isMain = true;
    }

    public void unsetMain() {
        this.isMain = false;
    }

    public void delete() {
        this.isDeleted = true;
    }
}
