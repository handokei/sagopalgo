package org.example.domain.cart.service;

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
import org.example.domain.user.exception.UserErrorCode;
import org.example.domain.user.exception.UserException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CartService {

    private final UserRepository userRepository;
    private final ProductRepository productRepository;
    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;

    @Transactional
    public void createCartItem(Long userId, CartCreateRequestDto requestDto) {

        userRepository.findByIdAndIsDeletedFalse(userId)
                        .orElseThrow(() -> new UserException(UserErrorCode.USER_NOT_FOUND_EXCEPTION));

        productRepository.findByIdAndIsDeletedFalse(requestDto.getProductId())
                .orElseThrow(() -> new ProductException(ProductErrorCode.PRODUCT_NOT_FOUND_EXCEPTION));

        Cart cart = cartRepository.findByOwnerTypeAndOwnerKey(OwnerType.USER, userId.toString())
                .orElseGet(() -> cartRepository.save(Cart.forUser(userId)));

        cart.addItem(requestDto.getProductId(),
                requestDto.getQuantity());

    }  public Page<CartResponseDto> getCartItems(Long userId, int page, int size) {
        userRepository.findByIdAndIsDeletedFalse(userId)
                .orElseThrow(() -> new UserException(UserErrorCode.USER_NOT_FOUND_EXCEPTION));

        Pageable pageable = PageRequest.of( page, size);

        Page<CartItem> cartItems = cartItemRepository.findByCartUserIdAndIsDeletedFalse(userId, pageable);

        return cartItems
                .map(cartItem -> {
                    String productTitle = productRepository.findTitleByIdAndIsDeletedFalse(cartItem.getProductId())
                            .orElseThrow(( ) -> new ProductException(ProductErrorCode.PRODUCT_NOT_FOUND_EXCEPTION));

                    return CartResponseDto.from(cartItem,productTitle);
                });
    }


    @Transactional
    public void deletedCartItem(Long cartItemId) {
        CartItem cartItem = cartItemRepository.findByIdAndIsDeletedFalse(cartItemId)
                .orElseThrow(() -> new CartItemException(CartItemErrorCode.CART_ITEM_NOT_FOUND_EXCEPTION));
        ;

        Cart cart = cartRepository.findById(cartItem.getId())
                .orElseThrow(() -> new CartException(CartErrorCode.CART_NOT_FOUND_EXCEPTION));

        cart.removeItem(cartItemId);
    }
}
