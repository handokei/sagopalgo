package org.example.domain.cart.domain.repository;

import org.example.domain.cart.domain.model.CartItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface CartItemRepository extends JpaRepository<CartItem , Long> {

}
