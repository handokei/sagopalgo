package org.example.domain.product.controller.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.example.domain.product.domain.model.ProductStatus;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class ProductStatusUpdateRequestDto {

    @NotNull
    private ProductStatus productStatus;
}
