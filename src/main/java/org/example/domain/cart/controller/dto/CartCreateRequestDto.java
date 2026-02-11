package org.example.domain.cart.controller.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class CartCreateRequestDto {

    @NotNull
    private Long productId;

    @NotNull
    private int quantity;
}
