package org.example.domain.review.domain.repository;

import org.example.domain.review.domain.model.Review;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {

    Optional<Review> findByIdAndIsDeletedFalse(Long id);

    Optional<Review> findByUserIdAndProductIdAndIsDeletedFalse(Long userId, Long productId);

    Page<Review> findByProductIdAndIsDeletedFalseOrderByCreatedAtDesc(Long productId, Pageable pageable);

    Page<Review> findByUserIdAndIsDeletedFalseOrderByCreatedAtDesc(Long userId, Pageable pageable);

    boolean existsByUserIdAndProductIdAndIsDeletedFalse(Long userId, Long productId);

    @Query("""
            SELECT AVG(r.rating), COUNT(r)
            FROM Review r
            WHERE r.productId = :productId AND r.isDeleted = false
            """)
    Object[] findRatingSummaryByProductId(Long productId);
}
