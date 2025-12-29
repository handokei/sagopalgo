package org.example.domain.cart.domain.repository;

import org.example.domain.cart.domain.model.CartItem;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CartItemRepository extends JpaRepository<CartItem , Long> {
    Page<CartItem> findByCartUserIdAndIsDeletedFalse(Long userId, Pageable pageable);

    Optional<CartItem> findByIdAndIsDeletedFalse(Long id);
}
