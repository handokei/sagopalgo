package org.example.domain.product.controller.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.example.domain.product.domain.model.ProductImage;

@Getter
@AllArgsConstructor
public class ProductImageResponseDto {

    private Long id;
    private String imageUrl;
    private String originalFileName;
    private int sortOrder;
    private boolean isMain;

    public static ProductImageResponseDto from(ProductImage image) {
        return new ProductImageResponseDto(
                image.getId(),
                "/api/products/images/" + image.getImageUrl(),
                image.getOriginalFileName(),
                image.getSortOrder(),
                image.isMain()
        );
    }
}
