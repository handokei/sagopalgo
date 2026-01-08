package org.example.domain.like.controller.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.example.domain.like.domain.model.ProductLike;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ProductLikeResponseDto {

    private Long productId;

    public static ProductLikeResponseDto from(ProductLike like) {
    return new ProductLikeResponseDto(like.getProductId());
    }
}
