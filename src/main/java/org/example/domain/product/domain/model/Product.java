package org.example.domain.product.domain.model;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.example.global.config.entity.BaseEntity;

import static org.example.domain.product.domain.model.ProductStatus.OUT_OF_STOCK;
import static org.example.domain.product.domain.model.ProductStatus.ON_SALE;
import org.example.domain.product.exception.ProductErrorCode;
import org.example.domain.product.exception.ProductException;

@Entity
@Table(name = "products")
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
public class Product extends BaseEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;

    private String contents;

    private int price;

    private int stock;

    @Enumerated(EnumType.STRING)
    private ProductStatus productStatus;

    @Enumerated(EnumType.STRING)
    private ProductCategory productCategory;

    private boolean isDeleted = false;

    private Product(String title, String contents, int price, int stock, ProductStatus productStatus, ProductCategory productCategory) {
        this.title = title;
        this.contents = contents;
        this.price = price;
        if (price <= 0 ) {
            throw new ProductException(ProductErrorCode.VALID_NON_ZERO_PRICE);
        }
        if (stock < 0 ) {
            throw new ProductException(ProductErrorCode.VALID_NON_ZERO_STOCK);
        }
        this.stock = stock;
        if (productStatus == OUT_OF_STOCK && stock != 0 ) {
            throw new ProductException(ProductErrorCode.INVALID_OUT_OF_STOCK_STATUS_CHANGE);
        }
        if (productStatus == ON_SALE && stock <= 0 ) {
            throw new ProductException(ProductErrorCode.INVALID_OUT_OF_STOCK_STATUS_CHANGE);
        }
        this.productStatus = productStatus;
        if (productCategory == null ) {
            throw new ProductException(ProductErrorCode.VALID_NON_NULL_PRODUCT_CATEGORY);
        }
        this.productCategory = productCategory;
    }

    public static Product of(String title, String contents, int price, int stock, ProductStatus productStatus, ProductCategory productCategory) {

         return new Product(title,
                 contents,
                 price,
                 stock, productStatus,
                 productCategory);
    }

    public void update(String title,
                               String contents,
                               int price,
                               int stock,
                       ProductStatus productStatus,
                       ProductCategory productCategory){

        if (title != null) this.title = title;
        if (contents != null) this.contents = contents;
        if (price <= 0 ) {
            throw new ProductException(ProductErrorCode.VALID_NON_ZERO_PRICE);
        }
        if (stock < 0 ) {
            throw new ProductException(ProductErrorCode.VALID_NON_ZERO_STOCK);
        }

        this.price = price;

        this.stock = stock;
        if (productStatus == OUT_OF_STOCK && stock != 0 ) {
            throw new ProductException(ProductErrorCode.INVALID_OUT_OF_STOCK_STATUS_CHANGE);
        }
        if (productStatus == ON_SALE && stock <= 0 ) {
            throw new ProductException(ProductErrorCode.INVALID_OUT_OF_STOCK_STATUS_CHANGE);
        }
        if (productStatus != null) this.productStatus = productStatus;
        if (productCategory != null)this.productCategory = productCategory;
    }

    public void updateProductStatus(ProductStatus productStatus) {
        if (productStatus == OUT_OF_STOCK && stock != 0 ) {
            throw new ProductException(ProductErrorCode.INVALID_OUT_OF_STOCK_STATUS_CHANGE);
        }
        if (productStatus == ON_SALE && stock <= 0 ) {
            throw new ProductException(ProductErrorCode.INVALID_OUT_OF_STOCK_STATUS_CHANGE);
        }
        this.productStatus = productStatus;
    }

    public void delete() {
         this.isDeleted = true;
    }
}
