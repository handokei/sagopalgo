package org.example.domain.product.service;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.domain.product.controller.dto.*;
import org.example.domain.product.domain.model.Product;
import org.example.domain.product.domain.model.ProductCategory;
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

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ProductService {

    private final ProductRepository productRepository;
    private final UserRepository userRepository;


    @Transactional
    public ProductCreateResponseDto createProduct(Long userId, @Valid ProductCreateRequestDto requestDto) {
        var seller = userRepository.findByIdAndIsDeletedFalse(userId)
                .orElseThrow(() -> new UserException(UserErrorCode.USER_NOT_FOUND_EXCEPTION));

        if (productRepository.existsByTitle(requestDto.getTitle())) {
            throw new ProductException(ProductErrorCode.DUPLICATE_PRODUCT_TITLE);
        }

        Product product = Product.of(
                seller,
                requestDto.getTitle(),
                requestDto.getContents(),
                requestDto.getPrice(),
                requestDto.getStock(),
                requestDto.getProductStatus(),
                requestDto.getProductCategory());

        productRepository.save(product);

        return ProductCreateResponseDto.from(product);
    }


    public Page<ProductResponseDto> getProducts(int page, int size, String sort, ProductCategory productCategory, String keyword) {

        Pageable pageable = PageRequest.of(page, size);


        return productRepository.search(
                pageable,
                sort,
                productCategory,
                keyword
        ).map(ProductResponseDto::from);
    }

    public ProductResponseDto readOne(Long id) {

        Product product = productRepository.findByIdAndIsDeletedFalse(id)
                .orElseThrow(() -> new ProductException(ProductErrorCode.PRODUCT_NOT_FOUND_EXCEPTION));

        
        return ProductResponseDto.from(product);
    }

    @Transactional
    public ProductResponseDto update(Long productId, Long userId, ProductUpdateRequestDto requestDto) {
        userRepository.findByIdAndIsDeletedFalse(userId)
                .orElseThrow(() -> new UserException(UserErrorCode.USER_NOT_FOUND_EXCEPTION));

        Product product = productRepository.findByIdAndIsDeletedFalse(productId)
                .orElseThrow(() -> new ProductException(ProductErrorCode.PRODUCT_NOT_FOUND_EXCEPTION));

        product.validateSeller(userId);

        product.update(requestDto.getTitle(),
                requestDto.getContents(),
                requestDto.getPrice(),
                requestDto.getStock(),
                requestDto.getProductStatus(),
                requestDto.getProductCategory());

        return ProductResponseDto.from(product);
    }

    @Transactional
    public void deleted(Long productId, Long userId) {
        userRepository.findByIdAndIsDeletedFalse(userId)
                .orElseThrow(() -> new UserException(UserErrorCode.USER_NOT_FOUND_EXCEPTION));

        Product product = productRepository.findByIdAndIsDeletedFalse(productId)
                .orElseThrow(() -> new ProductException(ProductErrorCode.PRODUCT_NOT_FOUND_EXCEPTION));

        product.validateSeller(userId);

        product.delete();
    }

    @Transactional
    public ProductResponseDto updateStatus(Long productId, Long userId, ProductStatusUpdateRequestDto requestDto) {
        userRepository.findByIdAndIsDeletedFalse(userId)
                .orElseThrow(() -> new UserException(UserErrorCode.USER_NOT_FOUND_EXCEPTION));

        Product product = productRepository.findByIdAndIsDeletedFalse(productId)
                .orElseThrow(() -> new ProductException(ProductErrorCode.PRODUCT_NOT_FOUND_EXCEPTION));

        product.validateSeller(userId);

        product.updateProductStatus(requestDto.getProductStatus());

        return ProductResponseDto.from(product);
    }
}
