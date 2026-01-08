package org.example.domain.like.service;

import lombok.RequiredArgsConstructor;
import org.example.domain.like.domain.model.ProductLike;
import org.example.domain.like.domain.repository.ProductLikeRepository;
import org.example.domain.product.domain.repository.ProductRepository;
import org.example.domain.product.exception.ProductErrorCode;
import org.example.domain.product.exception.ProductException;
import org.example.domain.user.domain.repository.UserRepository;
import org.example.domain.user.exception.UserErrorCode;
import org.example.domain.user.exception.UserException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ProductLikeService {

    private final ProductLikeRepository productLikeRepository;
    private final UserRepository userRepository;
    private final ProductRepository productRepository;

    public void createLike(Long id, Long userId) {
        productRepository.findByIdAndIsDeletedFalse(id)
                .orElseThrow(() -> new ProductException(ProductErrorCode.PRODUCT_NOT_FOUND_EXCEPTION));

        userRepository.findByIdAndIsDeletedFalse(userId)
                .orElseThrow(() -> new UserException(UserErrorCode.USER_NOT_FOUND_EXCEPTION));

        Optional<ProductLike> like = productLikeRepository.findByProductIdAndUserId(id, userId);

        if (like.isPresent()) {
            productLikeRepository.delete(like.get());
        } else {
            productLikeRepository.save(ProductLike.of(id,userId));
        }
    }
}
