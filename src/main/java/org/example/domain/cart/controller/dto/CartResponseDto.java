package org.example.domain.cart.controller.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.example.domain.cart.domain.model.CartItem;

@Getter
@AllArgsConstructor
public class CartResponseDto {

    private Long id;

    private Long productId;

    private String productTitle;

    private int productPrice;

    private String productImageUrl;

    private int quantity;

    public static CartResponseDto from(CartItem cartItem, String productTitle, int productPrice, String productImageUrl) {
        return new CartResponseDto(
                cartItem.getId(),
                cartItem.getProductId(),
                productTitle,
                productPrice,
                productImageUrl,
                cartItem.getQuantity()
        );
    }
}
