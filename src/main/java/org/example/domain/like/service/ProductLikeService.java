package org.example.domain.like.service;

import lombok.RequiredArgsConstructor;
import org.example.domain.like.controller.dto.ProductLikeResponseDto;
import org.example.domain.like.controller.dto.ProductLikesCreateResponseDto;
import org.example.domain.like.domain.model.ProductLike;
import org.example.domain.like.domain.repository.ProductLikeRepository;
import org.example.domain.product.domain.repository.ProductRepository;
import org.example.domain.product.exception.ProductErrorCode;
import org.example.domain.product.exception.ProductException;
import org.example.domain.user.domain.repository.UserRepository;
import org.example.domain.user.exception.UserErrorCode;
import org.example.domain.user.exception.UserException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ProductLikeService {

    private final ProductLikeRepository productLikeRepository;
    private final UserRepository userRepository;
    private final ProductRepository productRepository;

    @Transactional
    public ProductLikesCreateResponseDto createLike(Long id, Long userId) {
        productRepository.findByIdAndIsDeletedFalse(id)
                .orElseThrow(() -> new ProductException(ProductErrorCode.PRODUCT_NOT_FOUND_EXCEPTION));

        userRepository.findByIdAndIsDeletedFalse(userId)
                .orElseThrow(() -> new UserException(UserErrorCode.USER_NOT_FOUND_EXCEPTION));

        Optional<ProductLike> like = productLikeRepository.findByProductIdAndUserId(id, userId);

        if (like.isPresent()) {
            productLikeRepository.delete(like.get());
            return ProductLikesCreateResponseDto.from(false);
        } else {
            productLikeRepository.save(ProductLike.of(userId,id));
        }
        return ProductLikesCreateResponseDto.from(true);
    }

    public Page<ProductLikeResponseDto> getProductLikes(Long userId, int page, int size) {

        userRepository.findByIdAndIsDeletedFalse(userId)
                .orElseThrow(() -> new UserException(UserErrorCode.USER_NOT_FOUND_EXCEPTION));

        PageRequest pageRequest = PageRequest.of(page, size);

        return productLikeRepository
                .findAllByUserId(userId, pageRequest)
                .map(like -> {
                    var product = productRepository.findByIdAndIsDeletedFalse(like.getProductId())
                            .orElseThrow(() -> new ProductException(ProductErrorCode.PRODUCT_NOT_FOUND_EXCEPTION));
                    return ProductLikeResponseDto.from(like.getId(), product);
                });
    }

    public boolean isLiked(Long productId, Long userId) {
        return productLikeRepository.findByProductIdAndUserId(productId, userId).isPresent();
    }
}
