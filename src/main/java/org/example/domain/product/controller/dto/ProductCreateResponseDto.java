package org.example.domain.product.controller.dto;

import lombok.Getter;
import org.example.domain.product.domain.model.Product;
import org.example.domain.product.domain.model.ProductStatus;

@Getter
public class ProductCreateResponseDto {

    private Long id;

    private Long sellerId;

    private String sellerNickname;

    private String title;

    private String contents;

    private int price;

    private int stock;

    private ProductStatus productStatus;

    private Long categoryId;

    private String categoryName;

    public ProductCreateResponseDto(Product product) {
        this.id = product.getId();
        this.sellerId = product.getSellerId();
        this.sellerNickname = product.getSeller().getNickname();
        this.title = product.getTitle();
        this.contents = product.getContents();
        this.price = product.getPrice();
        this.stock = product.getStock();
        this.productStatus = product.getProductStatus();
        this.categoryId = product.getCategoryId();
        this.categoryName = product.getCategory() != null ? product.getCategory().getName() : null;
    }

    public static ProductCreateResponseDto from(Product product) {
        return new ProductCreateResponseDto(product);
    }
}
