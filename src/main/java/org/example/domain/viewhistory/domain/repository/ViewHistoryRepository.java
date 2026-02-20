package org.example.domain.viewhistory.domain.repository;

import org.example.domain.viewhistory.domain.model.ViewHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ViewHistoryRepository extends JpaRepository<ViewHistory, Long> {

    boolean existsByUserIdAndProductId(Long userId, Long productId);

    List<ViewHistory> findByUserId(Long userId);

    @Query("SELECT DISTINCT v.userId FROM ViewHistory v WHERE v.productId = :productId")
    List<Long> findUserIdsByProductId(Long productId);

    @Query("SELECT DISTINCT v.userId FROM ViewHistory v WHERE v.categoryId = :categoryId")
    List<Long> findUserIdsByCategoryId(Long categoryId);
}
