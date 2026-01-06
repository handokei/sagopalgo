package org.example.domain.product.controller.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.example.domain.product.domain.model.Product;
import org.example.domain.product.domain.model.ProductCategory;
import org.example.domain.product.domain.model.ProductStatus;

@Getter
public class ProductResponseDto {

    private Long id;

    private String title;

    private String contents;

    private int price;

    private int stock;

    private ProductStatus productStatus;

    private ProductCategory productCategory;

    public ProductResponseDto(Product product) {
        this.id = product.getId();
        this.title = product.getTitle();
        this.contents = product.getContents();
        this.price = product.getPrice();
        this.stock = product.getStock();
        this.productStatus = product.getProductStatus();
        this.productCategory = product.getProductCategory();
    }

    public static ProductResponseDto from(Product product) {
    return new ProductResponseDto(product
    );

    }
}
