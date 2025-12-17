package org.example.domain.product.controller.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.example.domain.product.domain.model.ProductCategory;
import org.example.domain.product.domain.model.ProductStatus;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ProductCreateRequestDto {

    @NotBlank
    @Size(min = 4, max = 30)
    private String title;

    @NotBlank
    @Size(min = 10, max = 200)
    private String contents;

    @NotNull
    private int price;

    @NotNull
    private int stock;

    @NotNull
    private ProductStatus productStatus;

    @NotNull
    private ProductCategory productCategory;


}
