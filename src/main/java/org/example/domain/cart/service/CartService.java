package org.example.domain.cart.service;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.domain.cart.controller.dto.CartCreateRequestDto;
import org.example.domain.cart.controller.dto.CartResponseDto;
import org.example.domain.cart.domain.model.Cart;
import org.example.domain.cart.domain.model.CartItem;
import org.example.domain.cart.domain.model.OwnerType;
import org.example.domain.cart.domain.repository.CartItemRepository;
import org.example.domain.cart.domain.repository.CartRepository;
import org.example.domain.cart.exception.CartErrorCode;
import org.example.domain.cart.exception.CartException;
import org.example.domain.cart.exception.CartItemErrorCode;
import org.example.domain.cart.exception.CartItemException;
import org.example.domain.product.domain.repository.ProductRepository;
import org.example.domain.product.exception.ProductErrorCode;
import org.example.domain.product.exception.ProductException;
import org.example.domain.user.domain.repository.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CartService {

    private final UserRepository userRepository;
    private final ProductRepository productRepository;
    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;



    @Transactional
    public void addItemToCart(OwnerType ownerType, String ownerKey, @Valid CartCreateRequestDto requestDto) {

        productRepository.findByIdAndIsDeletedFalse(requestDto.getProductId())
                .orElseThrow(() -> new ProductException(ProductErrorCode.PRODUCT_NOT_FOUND_EXCEPTION));

        Cart cart = cartRepository.findByOwnerTypeAndOwnerKey(ownerType, ownerKey)
                .orElseGet(() -> cartRepository.save(
                        Cart.create(ownerType,ownerKey)
                ));

        cart.addItem(
                requestDto.getProductId(),
                requestDto.getQuantity()
        );
    }

    public Page<CartResponseDto> getCartItems(OwnerType ownerType, String ownerKey, int page, int size) {

        Cart cart = cartRepository.findByOwnerTypeAndOwnerKey(ownerType, ownerKey)
                .orElseThrow(() -> new CartException(CartErrorCode.CART_NOT_FOUND_EXCEPTION));

        List<CartResponseDto> allItems = cart.getCartItems().stream()
                .map(cartItem -> {
                    String productTitle = productRepository.findTitleByIdAndIsDeletedFalse(cartItem.getProductId())
                            .orElseThrow(() -> new ProductException(ProductErrorCode.PRODUCT_NOT_FOUND_EXCEPTION));

                    return CartResponseDto.from(cartItem, productTitle);
                })
                .toList();


        Pageable pageable = PageRequest.of(page, size);

        int start = (int)pageable.getOffset();
        int end = Math.min(start + pageable.getPageSize(), allItems.size());

        List<CartResponseDto> contents = start >= allItems.size()
                ? List.of()
                : allItems.subList(start, end);

        return new PageImpl<>(
                contents,
                pageable,
                allItems.size()
        );
    }

    @Transactional
    public void deletedCartItem(OwnerType ownerType, String ownerKey, Long cartItemId) {

        Cart cart = cartRepository.findByOwnerTypeAndOwnerKey(ownerType, ownerKey)
                .orElseThrow(() -> new CartException(CartErrorCode.CART_NOT_FOUND_EXCEPTION));

        boolean removed = cart.removeItem(cartItemId);
        if (!removed) {
            throw new CartItemException(CartItemErrorCode.CART_ITEM_NOT_FOUND_EXCEPTION);
        }


    }

    @Transactional
    public void updateQuantity(OwnerType ownerType, String ownerKey, Long cartItemId, int quantity) {

        Cart cart = cartRepository.findByCartItems_Id(cartItemId)
                .orElseThrow(() -> new CartException(CartErrorCode.CART_NOT_FOUND_EXCEPTION));


        cart.validateOwnership(ownerType,ownerKey);
        CartItem cartItem = cart.getCartItems().stream()
                .filter(item -> item.getId().equals(cartItemId))
                .findFirst()
                .orElseThrow(() -> new CartItemException(CartItemErrorCode.CART_ITEM_NOT_FOUND_EXCEPTION));

        cartItem.changeQuantity(quantity);
    }
}
