package org.example.domain.cart.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.domain.cart.controller.dto.CartCreateRequestDto;
import org.example.domain.cart.controller.dto.CartItemUpdateRequestDto;
import org.example.domain.cart.controller.dto.CartResponseDto;
import org.example.domain.cart.domain.model.OwnerType;
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

    //로그인 유저
    @PostMapping("/me/items")
    public ResponseEntity<Void> create(
            @AuthenticationPrincipal CustomUserDetails userDetails,
           @RequestBody @Valid CartCreateRequestDto requestDto
            ) {
        cartService.addItemToCart(OwnerType.USER,
                userDetails.getId().toString(),
                requestDto);

        return ResponseEntity.ok().build();
    }

    @GetMapping("/me")
    public ResponseEntity<Page<CartResponseDto>> getCartItems(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Page<CartResponseDto> responseDto = cartService.getCartItems(
                OwnerType.USER,
                userDetails.getId().toString(),
                page,
                size);

        return ResponseEntity.ok().body(responseDto);
    }

    @DeleteMapping("/me/items/{id}")
    public ResponseEntity<Void> deletedCartItem(
          @PathVariable Long id,
          @AuthenticationPrincipal CustomUserDetails userDetails
  )       {


      cartService.deletedCartItem(OwnerType.USER,
              userDetails.getId().toString(),
              id);

        return ResponseEntity.noContent().build();
  }

  //수량 변경 해야함. patch
    @PatchMapping("/me/items/{id}")
    public ResponseEntity<Void> updateCartItemQuantity(
            @PathVariable Long id,
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestBody @Valid CartItemUpdateRequestDto requestDto
    ) {
        cartService.updateQuantity(
                OwnerType.USER,
                userDetails.getId().toString(),
                id,
                requestDto.getQuantity());
        return ResponseEntity.noContent().build();
    }




    /**
     * 비로그인 Guest
     */
    //비로그인 유저

    @PostMapping("/guest/items")
    public ResponseEntity<Void> createForGuest(
            @CookieValue(name = "guestKey") String guestKey,
            @RequestBody @Valid CartCreateRequestDto requestDto
    ) {
        cartService.addItemToCart(OwnerType.GUEST,
                guestKey,
                requestDto);

        return ResponseEntity.ok().build();
    }

    @GetMapping("/guest")
    public ResponseEntity<Page<CartResponseDto>> getCartItemsForGuest(
            @CookieValue(name = "guestKey") String guestKey,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Page<CartResponseDto> responseDto = cartService.getCartItems(
                OwnerType.GUEST,
                guestKey,
                page,
                size);

        return ResponseEntity.ok().body(responseDto);
    }

    @DeleteMapping("/guest/items/{id}")
    public ResponseEntity<Void> deletedCartItemForGuest(
            @PathVariable Long id,
            @CookieValue(name = "guestKey") String guestKey
    )       {


        cartService.deletedCartItem(OwnerType.GUEST,
                guestKey,
                id);

        return ResponseEntity.noContent().build();
    }

    //수량 변경 해야함. patch
    @PatchMapping("/guest/items/{id}")
    public ResponseEntity<Void> updateCartItemQuantityForGuest(
            @PathVariable Long id,
            @CookieValue(name = "guestKey") String guestKey,
            @RequestBody @Valid CartItemUpdateRequestDto requestDto
    ) {
        cartService.updateQuantity(
                OwnerType.GUEST,
                guestKey,
                id,
                requestDto.getQuantity());
        return ResponseEntity.noContent().build();
    }


}
