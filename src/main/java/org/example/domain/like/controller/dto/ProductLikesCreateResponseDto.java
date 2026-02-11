package org.example.domain.like.controller.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.example.domain.like.domain.model.ProductLike;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ProductLikesCreateResponseDto {

    private boolean liked;

    public static ProductLikesCreateResponseDto from(boolean liked) {
        return new ProductLikesCreateResponseDto(liked);
    }
}
