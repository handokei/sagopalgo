package org.example.domain.cart.domain.repository;

import org.example.domain.cart.domain.model.Cart;
import org.example.domain.cart.domain.model.OwnerType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;


@Repository
public interface CartRepository extends JpaRepository<Cart, Long> {

    @Query("SELECT c FROM Cart c LEFT JOIN FETCH c.cartItems WHERE c.ownerType = :ownerType AND c.ownerKey = :ownerKey")
    Optional<Cart> findByOwnerTypeAndOwnerKey(OwnerType ownerType, String ownerKey);

    Optional<Cart> findByCartItems_Id(Long cartItemId);
}
