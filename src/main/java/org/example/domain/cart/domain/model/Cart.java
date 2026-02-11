package org.example.domain.cart.domain.model;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.example.domain.cart.exception.CartErrorCode;
import org.example.domain.cart.exception.CartException;
import org.example.global.config.entity.BaseEntity;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "carts")
@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class Cart extends BaseEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String ownerKey;

    @Enumerated(EnumType.STRING)
    private OwnerType ownerType;



    @OneToMany(cascade = {CascadeType.PERSIST,CascadeType.REMOVE},
    orphanRemoval = true)
    @JoinColumn(name = "cart_id")
    private List<CartItem> cartItems = new ArrayList<>();


    protected Cart(OwnerType ownerType, String ownerKey) {
        this.ownerKey = ownerKey;
        this.ownerType = ownerType;
    }


    public static Cart create(OwnerType ownerType, String ownerKey) {
        return new Cart(
                ownerType,
                ownerKey
        );
    }


    public static Cart forUser(Long userId){
        return new Cart(OwnerType.USER, userId.toString());

    }


    public static Cart forGuest(String guestKey) {
        return new Cart(OwnerType.GUEST, guestKey);
    }

    public void addItem(Long productId, int quantity) {
        CartItem item = cartItems.stream()
                .filter(ci -> ci.isSameProduct(productId))
                .findFirst()
                .orElseGet(() -> {
                    CartItem newItem = CartItem.of(productId, 0);
                    cartItems.add(newItem);
                    return newItem;
                });

        item.increase(quantity);
    }

    public boolean removeItem(Long cartItemId) {
        boolean removed = cartItems.removeIf(i -> i.getId().equals(cartItemId));

        return removed;
    }

    public void validateOwnership(OwnerType ownerType, String ownerKey) {
        if (this.ownerType!= ownerType || !this.ownerKey.equals(ownerKey)) {
            throw new CartException(CartErrorCode.CART_ACCESS_DENIED);
        }
    }

}
