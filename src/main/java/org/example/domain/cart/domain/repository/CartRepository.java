package org.example.domain.cart.domain.repository;

import org.example.domain.cart.domain.model.Cart;
import org.example.domain.cart.domain.model.OwnerType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;


@Repository
public interface CartRepository extends JpaRepository<Cart, Long> {
    Optional<Cart> findByUserId(Long userId);

    Optional<Cart> findByOwnerTypeAndOwnerKey(OwnerType ownerType, String string);
}
