package org.example.domain.cart.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.domain.cart.controller.dto.CartCreateRequestDto;
import org.example.domain.cart.controller.dto.CartResponseDto;
import org.example.domain.cart.service.CartService;
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

    @PostMapping
    public ResponseEntity<String> create(
            @AuthenticationPrincipal CustomUserDetails userDetails,
           @RequestBody @Valid CartCreateRequestDto requestDto
            ) {
        Long userId = userDetails.getId();
        cartService.createCartItem(userId,requestDto);
        return ResponseEntity.ok("장바구니에 추가 되었습니다.");
    }


    @GetMapping
    public ResponseEntity<Page<CartResponseDto>> getCartItems(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Long userId = userDetails.getId();
        Page<CartResponseDto> responseDto = cartService.getCartItems(userId,page, size);
        return ResponseEntity.ok().body(responseDto);
    }

  @DeleteMapping("/items/{cartItemId}")
  public ResponseEntity<Void> deletedCartItem(
          @PathVariable Long cartItemId
  )       {
        cartService.deletedCartItem(cartItemId);
        return ResponseEntity.noContent().build();
  }
}
