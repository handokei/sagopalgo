package org.example.domain.like.domain.repository;

import org.example.domain.like.domain.model.ProductLike;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductLikeRepository extends JpaRepository<ProductLike, Long> {
    Optional<ProductLike> findByProductIdAndUserId(Long id, Long userId);

    Page<ProductLike> findAllByUserId(Long userId, PageRequest pageRequest);

    @Query("SELECT pl.userId FROM ProductLike pl WHERE pl.productId = :productId")
    List<Long> findUserIdsByProductId(Long productId);
}
