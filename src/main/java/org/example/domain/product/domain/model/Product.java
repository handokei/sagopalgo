package org.example.domain.product.domain.model;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.example.domain.category.domain.model.Category;
import org.example.domain.user.domain.model.User;
import org.example.global.config.entity.BaseEntity;

import static org.example.domain.product.domain.model.ProductStatus.OUT_OF_STOCK;
import static org.example.domain.product.domain.model.ProductStatus.ON_SALE;
import org.example.domain.product.exception.ProductErrorCode;
import org.example.domain.product.exception.ProductException;

@Entity
@Table(name = "products", indexes = {
        @Index(name = "idx_product_seller_id", columnList = "seller_id"),
        @Index(name = "idx_product_category_id", columnList = "category_id"),
        @Index(name = "idx_product_is_deleted", columnList = "isDeleted")
})
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class Product extends BaseEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "seller_id", nullable = false)
    private Long sellerId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "seller_id", insertable = false, updatable = false)
    private User seller;

    private String title;

    private String contents;

    private int price;

    private int stock;

    @Enumerated(EnumType.STRING)
    private ProductStatus productStatus;

    @Column(name = "category_id")
    private Long categoryId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", insertable = false, updatable = false)
    private Category category;

    private boolean isDeleted = false;

    private Product(User seller, String title, String contents, int price, int stock, ProductStatus productStatus, Category category) {
        if (seller == null) {
            throw new ProductException(ProductErrorCode.SELLER_REQUIRED);
        }
        this.sellerId = seller.getId();
        this.seller = seller;
        this.title = title;
        this.contents = contents;
        if (price <= 0) {
            throw new ProductException(ProductErrorCode.VALID_NON_ZERO_PRICE);
        }
        this.price = price;
        if (stock < 0) {
            throw new ProductException(ProductErrorCode.VALID_NON_ZERO_STOCK);
        }
        this.stock = stock;
        if (productStatus == OUT_OF_STOCK && stock != 0) {
            throw new ProductException(ProductErrorCode.INVALID_OUT_OF_STOCK_STATUS_CHANGE);
        }
        if (productStatus == ON_SALE && stock <= 0) {
            throw new ProductException(ProductErrorCode.INVALID_OUT_OF_STOCK_STATUS_CHANGE);
        }
        this.productStatus = productStatus;
        if (category == null) {
            throw new ProductException(ProductErrorCode.VALID_NON_NULL_PRODUCT_CATEGORY);
        }
        this.categoryId = category.getId();
        this.category = category;
    }

    public static Product of(User seller, String title, String contents, int price, int stock, ProductStatus productStatus, Category category) {
        return new Product(seller, title, contents, price, stock, productStatus, category);
    }

    public void validateSeller(Long userId) {
        if (!this.sellerId.equals(userId)) {
            throw new ProductException(ProductErrorCode.NOT_SELLER_OF_PRODUCT);
        }
    }

    public boolean isSeller(Long userId) {
        return this.sellerId.equals(userId);
    }

    public void update(String title,
                       String contents,
                       int price,
                       int stock,
                       ProductStatus productStatus,
                       Category category) {

        if (title != null) this.title = title;
        if (contents != null) this.contents = contents;
        if (price <= 0) {
            throw new ProductException(ProductErrorCode.VALID_NON_ZERO_PRICE);
        }
        if (stock < 0) {
            throw new ProductException(ProductErrorCode.VALID_NON_ZERO_STOCK);
        }

        this.price = price;

        this.stock = stock;
        if (productStatus == OUT_OF_STOCK && stock != 0) {
            throw new ProductException(ProductErrorCode.INVALID_OUT_OF_STOCK_STATUS_CHANGE);
        }
        if (productStatus == ON_SALE && stock <= 0) {
            throw new ProductException(ProductErrorCode.INVALID_OUT_OF_STOCK_STATUS_CHANGE);
        }
        if (productStatus != null) this.productStatus = productStatus;
        if (category != null) {
            this.categoryId = category.getId();
            this.category = category;
        }
    }

    public void updateProductStatus(ProductStatus productStatus) {
        if (productStatus == OUT_OF_STOCK && stock != 0) {
            throw new ProductException(ProductErrorCode.INVALID_OUT_OF_STOCK_STATUS_CHANGE);
        }
        if (productStatus == ON_SALE && stock <= 0) {
            throw new ProductException(ProductErrorCode.INVALID_OUT_OF_STOCK_STATUS_CHANGE);
        }
        this.productStatus = productStatus;
    }

    public void delete() {
         this.isDeleted = true;
    }

    public void decreaseStock(int quantity) {
        if (this.productStatus != ON_SALE) {
            throw new ProductException(ProductErrorCode.PRODUCT_NOT_ON_SALE);
        }
        if (this.stock < quantity) {
            throw new ProductException(ProductErrorCode.INSUFFICIENT_STOCK);
        }
        this.stock -= quantity;
        if (this.stock == 0) {
            this.productStatus = OUT_OF_STOCK;
        }
    }

    public void restoreStock(int quantity) {
        this.stock += quantity;
        if (this.productStatus == OUT_OF_STOCK && this.stock > 0) {
            this.productStatus = ON_SALE;
        }
    }

    public StockStatus getStockStatus() {
        if (this.stock == 0) {
            return StockStatus.OUT_OF_STOCK;
        } else if (this.stock < 5) {
            return StockStatus.LOW_STOCK;
        } else {
            return StockStatus.IN_STOCK;
        }
    }
}
