package org.example.domain.product.domain.repository;


import org.example.domain.product.domain.model.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ProductCustomRepository {
    Page<Product> search(int likeCount, Pageable pageable);
}
