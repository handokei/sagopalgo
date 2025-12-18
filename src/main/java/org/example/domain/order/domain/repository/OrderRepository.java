package org.example.domain.order.domain.repository;

import org.example.domain.order.domain.model.Order;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    Optional<Order> findByIdAndIsDeletedFalse(Long id);

    Page<Order> findByUserIdAndIsDeletedFalse(Long userId, Pageable pageable);
}
