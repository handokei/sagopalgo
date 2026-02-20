package org.example.domain.product.controller.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.example.domain.product.domain.model.ProductStatus;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ProductUpdateRequestDto {

    @NotBlank
    private String title;

    @NotBlank
    private String contents;

    @NotNull
    private int price;

    @NotNull
    private int stock;

    @NotNull
    private ProductStatus productStatus;

    @NotNull
    private Long categoryId;
}
