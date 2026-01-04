package org.example.domain.cart.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.domain.cart.controller.dto.CartCreateRequestDto;
import org.example.domain.cart.controller.dto.CartItemUpdateRequestDto;
import org.example.domain.cart.controller.dto.CartResponseDto;
import org.example.domain.cart.service.CartService;
import org.example.domain.cart.support.CartOwner;
import org.example.domain.cart.support.CartOwnerResolver;
import org.example.global.security.jwt.CustomUserDetails;

import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/carts")
@RequiredArgsConstructor
public class CartController {

    private final CartService cartService;
    private final CartOwnerResolver cartOwnerResolver;

    @PostMapping("/items")
    public ResponseEntity<String> create(
            @AuthenticationPrincipal CustomUserDetails userDetails,
           @CookieValue(value = "guestKey", required = false) String guestKey,
           @RequestBody @Valid CartCreateRequestDto requestDto
            ) {
        CartOwner owner = cartOwnerResolver.resolve(userDetails, guestKey);

        cartService.addItemToCart(owner.ownerType(),
                owner.ownerKey(),
                requestDto);

        return ResponseEntity.ok("장바구니에 추가 되었습니다.");
    }


    @GetMapping
    public ResponseEntity<Page<CartResponseDto>> getCartItems(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @CookieValue(value = "guestKey", required = false) String guestKey,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {

        CartOwner owner = cartOwnerResolver.resolve(userDetails,
                guestKey);

        Page<CartResponseDto> responseDto = cartService.getCartItems(owner.ownerType(),
                owner.ownerKey(),
                page,
                size);

        return ResponseEntity.ok().body(responseDto);
    }

  @DeleteMapping("/items/{cartItemId}")
  public ResponseEntity<Void> deletedCartItem(
          @PathVariable Long cartItemId,
          @AuthenticationPrincipal CustomUserDetails userDetails,
          @CookieValue(value = "guestKey", required = false) String guestKey
  )       {

      CartOwner owner = cartOwnerResolver.resolve(userDetails, guestKey);

      cartService.deletedCartItem(owner.ownerType(),owner.ownerKey(),cartItemId);
        return ResponseEntity.noContent().build();
  }

  //수량 변경 해야함. patch
    @PatchMapping("/items/{cartItemId}")
    public ResponseEntity<Void> updateCartItemQuantity(
            @PathVariable Long cartItemId,
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @CookieValue(value = "guestKey", required = false) String guestKey,
            @RequestBody @Valid CartItemUpdateRequestDto requestDto
    ) {
        CartOwner owner = cartOwnerResolver.resolve(userDetails, guestKey);

        cartService.updateQuantity(owner.ownerType(),owner.ownerKey(),cartItemId,requestDto.getQuantity());
        return ResponseEntity.noContent().build();
    }


}
