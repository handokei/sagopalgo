package org.example.domain.like.controller.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.example.domain.product.domain.model.Product;
import org.example.domain.product.domain.model.ProductStatus;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ProductLikeResponseDto {

    private Long id;
    private Long productId;
    private String productTitle;
    private int productPrice;
    private ProductStatus productStatus;

    public static ProductLikeResponseDto from(Long likeId, Product product) {
        return new ProductLikeResponseDto(
                likeId,
                product.getId(),
                product.getTitle(),
                product.getPrice(),
                product.getProductStatus()
        );
    }
}
