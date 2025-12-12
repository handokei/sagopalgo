package org.example.domain.product.domain.model;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.example.global.config.entity.BaseEntity;

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

    private ProductStatus productStatus;

    private ProductCategory productCategory;

    private boolean isDeleted = false;

    private Product(String title, String contents, int price, int stock, ProductStatus productStatus, ProductCategory productCategory) {
        this.title = title;
        this.contents = contents;
        this.price = price;
        this.stock = stock;
        this.productStatus = productStatus;
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
         this.title = title;
         this.contents = contents;
         this.price = price;
         this.stock = stock;
         this.productStatus = productStatus;
         this.productCategory = productCategory;
    }

    public void delete() {
         this.isDeleted = true;
    }
}
