package org.example.domain.cart.controller.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class CartItemUpdateRequestDto {

    private int quantity;

    public CartItemUpdateRequestDto(int quantity) {
        this.quantity = quantity;
    }
}
