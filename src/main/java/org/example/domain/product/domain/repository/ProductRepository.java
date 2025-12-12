package org.example.domain.product.domain.repository;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.example.domain.product.domain.model.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    Optional<Product> findByIdAndIsDeletedFalse(Long id);

    Page<Product> findByIsDeletedFalse(Pageable pageable);

    boolean validateDuplicateTitle(String title);

    boolean validateNonZeroPrice(int price);

    boolean validateNonZeroStock(int stock);
}
