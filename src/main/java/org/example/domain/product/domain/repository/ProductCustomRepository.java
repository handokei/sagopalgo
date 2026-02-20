package org.example.domain.product.domain.repository;

import org.example.domain.product.domain.model.Product;
import org.example.domain.product.domain.model.ProductStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ProductCustomRepository {
    Page<Product> search(Pageable pageable, String sort, Long categoryId, String keyword,
                         Integer minPrice, Integer maxPrice, ProductStatus status);
}
