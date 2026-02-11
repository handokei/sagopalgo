package org.example.domain.product.domain.repository;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.example.domain.product.domain.model.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;


public interface ProductRepository extends JpaRepository<Product, Long>, ProductCustomRepository {

    @Query("""
            SELECT p FROM Product p
            JOIN FETCH p.seller
            WHERE p.id = :id AND p.isDeleted = false
            """)
    Optional<Product> findByIdAndIsDeletedFalse(Long id);

    Page<Product> findByIsDeletedFalse(Pageable pageable);

    boolean existsByTitle(String title);

    @Query("""
            SELECT p.title
            FROM Product p
            WHERE p.id = :id
            and p.isDeleted = FALSE
            """
    )
    Optional<String> findTitleByIdAndIsDeletedFalse(Long id);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("""
            SELECT p FROM Product p
            WHERE p.id = :id AND p.isDeleted = false
            """)
    Optional<Product> findByIdWithLock(Long id);
}
