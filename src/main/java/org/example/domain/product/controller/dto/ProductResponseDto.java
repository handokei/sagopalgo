package org.example.domain.product.controller.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import org.example.domain.product.domain.model.Product;
import org.example.domain.product.domain.model.ProductCategory;
import org.example.domain.product.domain.model.ProductStatus;
import org.example.domain.product.domain.model.StockStatus;

import java.io.Serializable;
import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@lombok.AllArgsConstructor
public class ProductResponseDto implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;

    private Long sellerId;

    private String sellerNickname;

    private String title;

    private String contents;

    private int price;

    private ProductStatus productStatus;

    private ProductCategory productCategory;

    private StockStatus stockStatus;

    private LocalDateTime createdAt;

    public ProductResponseDto(Product product) {
        this.id = product.getId();
        this.sellerId = product.getSellerId();
        this.sellerNickname = product.getSeller().getNickname();
        this.title = product.getTitle();
        this.contents = product.getContents();
        this.price = product.getPrice();
        this.productStatus = product.getProductStatus();
        this.productCategory = product.getProductCategory();
        this.stockStatus = product.getStockStatus();
        this.createdAt = product.getCreatedAt();
    }

    public static ProductResponseDto from(Product product) {
    return new ProductResponseDto(product
    );

    }
}
