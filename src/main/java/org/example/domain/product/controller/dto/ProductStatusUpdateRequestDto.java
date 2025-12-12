package org.example.domain.product.controller.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.example.domain.product.domain.model.ProductStatus;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class ProductStatusUpdateRequestDto {

    private ProductStatus productStatus;
}
