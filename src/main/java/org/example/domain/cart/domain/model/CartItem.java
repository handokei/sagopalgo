package org.example.domain.cart.domain.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class CartItem {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    private Long productId;

    private int quantity;

    protected CartItem(Long productId, int quantity) {
        this.productId = productId;
        this.quantity = quantity;
    }

    public static CartItem of(Long productId, int quantity) {
        return new CartItem(productId,quantity);
    }

    public boolean isSameProduct(Long productId){
        return this.productId.equals(productId);
    }

    public void increase(int quantity) {
        this.quantity += quantity;
    }

    public void decrease(int quantity) {
        this.quantity -= quantity;
    }




}
