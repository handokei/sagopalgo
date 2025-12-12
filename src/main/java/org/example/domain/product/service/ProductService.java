package org.example.domain.product.service;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.domain.product.controller.dto.*;
import org.example.domain.product.domain.model.Product;
import org.example.domain.product.domain.repository.ProductRepository;
import org.example.domain.product.exception.ProductErrorCode;
import org.example.domain.product.exception.ProductException;
import org.example.domain.user.domain.model.User;
import org.example.domain.user.domain.repository.UserRepository;
import org.example.domain.user.exception.UserErrorCode;
import org.example.domain.user.exception.UserException;
import org.springframework.boot.web.server.PortInUseException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import static org.example.domain.product.domain.model.ProductStatus.ON_SALE;
import static org.example.domain.product.domain.model.ProductStatus.OUT_OF_STOCK;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ProductService {

    private final ProductRepository productRepository;
    private final UserRepository userRepository;


    @Transactional
    public ProductCreateResponseDto createProduct(Long userId, @Valid ProductCreateRequestDto requestDto) {
         userRepository.findByIdAndIsDeletedFalse(userId)
                         .orElseThrow(() -> new UserException(UserErrorCode.USER_NOT_FOUND_EXCEPTION));

         if (productRepository.validateDuplicateTitle(requestDto.getTitle())) {
             throw new ProductException(ProductErrorCode.DUPLICATE_PRODUCT_TITLE);

         }

         if ((requestDto.getPrice() <= 0 )) {
             throw new ProductException(ProductErrorCode.VALID_NON_ZERO_PRICE);
         }

         if (requestDto.getStock() < 0) {
             throw new ProductException(ProductErrorCode.VALID_NON_ZERO_STOCK);
         }

        Product product = Product.of(requestDto.getTitle(),
                requestDto.getContents(),
                requestDto.getPrice(),
                requestDto.getStock(),
                requestDto.getProductStatus(),
                requestDto.getProductCategory());


        productRepository.save(product);

        return ProductCreateResponseDto.from(product);
    }


    public Page<ProductResponseDto> readAll(Pageable pageable) {

        Page<Product> products = productRepository.findByIsDeletedFalse(pageable);
        return products.map(ProductResponseDto::from);
    }

    public ProductResponseDto readOne(Long id) {

        Product product = productRepository.findByIdAndIsDeletedFalse(id)
                .orElseThrow(() -> new ProductException(ProductErrorCode.PRODUCT_NOT_FOUND_EXCEPTION));

        
        return ProductResponseDto.from(product);
    }

    @Transactional
    public ProductResponseDto update(Long productId, Long id, ProductUpdateRequestDto requestDto) {
        userRepository.findByIdAndIsDeletedFalse(id)
                .orElseThrow(() -> new UserException(UserErrorCode.USER_NOT_FOUND_EXCEPTION));

        Product product = productRepository.findByIdAndIsDeletedFalse(productId)
                .orElseThrow(() -> new ProductException(ProductErrorCode.PRODUCT_NOT_FOUND_EXCEPTION));

        if (requestDto.getPrice() <= 0) {
            throw new ProductException(ProductErrorCode.VALID_NON_ZERO_PRICE);
        }

        if (requestDto.getStock() < 0 ) {
            throw new ProductException(ProductErrorCode.VALID_NON_ZERO_STOCK);
        }

        if (productRepository.validateDuplicateTitle(requestDto.getTitle())) {
            throw new ProductException(ProductErrorCode.DUPLICATE_PRODUCT_TITLE);
        }

        product.update(requestDto.getTitle(),
                requestDto.getContents(),
                requestDto.getPrice(),
                requestDto.getStock());
        
        return ProductResponseDto.from(product);

    }

    @Transactional
    public void deleted(Long productId, Long id) {

        userRepository.findByIdAndIsDeletedFalse(id)
                .orElseThrow(() -> new UserException(UserErrorCode.USER_NOT_FOUND_EXCEPTION));

        Product product = productRepository.findByIdAndIsDeletedFalse(productId)
                .orElseThrow(() -> new ProductException(ProductErrorCode.PRODUCT_NOT_FOUND_EXCEPTION));

        product.delete();

    }

    public ProductResponseDto updateStatus(Long id, Long userId, ProductStatusUpdateRequestDto requestDto) {
        userRepository.findByIdAndIsDeletedFalse(userId)
                .orElseThrow(() -> new UserException(UserErrorCode.USER_NOT_FOUND_EXCEPTION));

        Product product = productRepository.findByIdAndIsDeletedFalse(id)
                .orElseThrow(() -> new ProductException(ProductErrorCode.PRODUCT_NOT_FOUND_EXCEPTION));

        if (requestDto.getProductStatus() == OUT_OF_STOCK) {
            if (product.getStock() != 0) {
                throw new ProductException(ProductErrorCode.INVALID_OUT_OF_STOCK_STATUS_CHANGE);
            }

        }
        else if (requestDto.getProductStatus() == ON_SALE) {
            if (product.getStock() <= 0) {
                throw new ProductException(ProductErrorCode.INVALID_OUT_OF_STOCK_STATUS_CHANGE);
            }
            else
        }
    }
}
