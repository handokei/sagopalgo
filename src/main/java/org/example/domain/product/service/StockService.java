package org.example.domain.product.service;

import lombok.RequiredArgsConstructor;
import org.example.domain.product.domain.model.Product;
import org.example.domain.product.domain.repository.ProductRepository;
import org.example.domain.product.exception.ProductErrorCode;
import org.example.domain.product.exception.ProductException;
import org.example.global.lock.DistributedLock;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class StockService {

    private final ProductRepository productRepository;

    @DistributedLock(key = "'product:' + #productId")
    @Transactional
    public Product decreaseStock(Long productId, int quantity) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ProductException(ProductErrorCode.PRODUCT_NOT_FOUND_EXCEPTION));

        product.decreaseStock(quantity);
        return product;
    }

    @DistributedLock(key = "'product:' + #productId")
    @Transactional
    public Product restoreStock(Long productId, int quantity) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ProductException(ProductErrorCode.PRODUCT_NOT_FOUND_EXCEPTION));

        product.restoreStock(quantity);
        return product;
    }
}
