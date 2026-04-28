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
import org.example.domain.product.domain.model.Product;
import org.example.domain.product.domain.model.ProductStatus;
import org.example.domain.product.domain.repository.ProductImageRepository;
import org.example.domain.product.service.ProductService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CartService {

    private final ProductService productService;
    private final ProductImageRepository productImageRepository;
    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;

    @Transactional
    public void addItemToCart(OwnerType ownerType, String ownerKey, @Valid CartCreateRequestDto requestDto) {

        Product product = productService.findProduct(requestDto.getProductId());

        if (product.getProductStatus() != ProductStatus.ON_SALE) {
            throw new CartException(CartErrorCode.NOT_SALE_PRODUCT_INVALID_ADD_CART_EXCEPTION);
        }

        if (ownerType == OwnerType.USER && product.isSeller(Long.parseLong(ownerKey))) {
            throw new CartException(CartErrorCode.OWN_PRODUCT_CART_EXCEPTION);
        }

        Cart cart = cartRepository.findByOwnerTypeAndOwnerKey(ownerType, ownerKey)
                .orElseGet(() -> cartRepository.save(
                        Cart.create(ownerType, ownerKey)
                ));

        cart.addItem(
                requestDto.getProductId(),
                requestDto.getQuantity()
        );
    }

    public Page<CartResponseDto> getCartItems(OwnerType ownerType, String ownerKey, int page, int size) {

        Cart cart = cartRepository.findByOwnerTypeAndOwnerKey(ownerType, ownerKey)
                .orElse(null);

        if (cart == null) {
            return new PageImpl<>(List.of(), PageRequest.of(page, size), 0);
        }

        List<CartResponseDto> allItems = cart.getCartItems().stream()
                .map(cartItem -> {
                    try {
                        Product product = productService.findProduct(cartItem.getProductId());
                        String imageUrl = productImageRepository
                                .findByProductIdAndIsMainTrueAndIsDeletedFalse(product.getId())
                                .map(img -> img.getImageUrl())
                                .orElse(null);
                        return CartResponseDto.from(cartItem, product.getTitle(), product.getPrice(), imageUrl);
                    } catch (Exception e) {
                        return null;
                    }
                })
                .filter(Objects::nonNull)
                .toList();

        Pageable pageable = PageRequest.of(page, size);

        int start = (int) pageable.getOffset();
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

        cart.validateOwnership(ownerType, ownerKey);
        CartItem cartItem = cart.getCartItems().stream()
                .filter(item -> item.getId().equals(cartItemId))
                .findFirst()
                .orElseThrow(() -> new CartItemException(CartItemErrorCode.CART_ITEM_NOT_FOUND_EXCEPTION));

        Product product = productService.findProduct(cartItem.getProductId());

        if (product.getProductStatus() != ProductStatus.ON_SALE) {
            throw new CartException(CartErrorCode.NOT_SALE_PRODUCT_QUANTITY_CHANGE_EXCEPTION);
        }

        cartItem.changeQuantity(quantity);
    }

    @Transactional
    public void clearCart(OwnerType ownerType, String ownerKey) {
        Cart cart = cartRepository.findByOwnerTypeAndOwnerKey(ownerType, ownerKey)
                .orElse(null);

        if (cart != null) {
            cart.getCartItems().clear();
        }
    }
}
