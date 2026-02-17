package org.example.domain.product.domain.repository;

import org.example.domain.product.domain.model.ProductImage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ProductImageRepository extends JpaRepository<ProductImage, Long> {

    List<ProductImage> findByProductIdAndIsDeletedFalseOrderBySortOrderAsc(Long productId);

    Optional<ProductImage> findByProductIdAndIsMainTrueAndIsDeletedFalse(Long productId);

    int countByProductIdAndIsDeletedFalse(Long productId);
}
