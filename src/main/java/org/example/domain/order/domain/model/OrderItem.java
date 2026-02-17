package org.example.domain.order.domain.model;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.example.global.config.entity.BaseEntity;


@Entity
@Table(name = "order_items")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class OrderItem extends BaseEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id")
    private Order order;

    private Long productId;

    private String productTitle;

    private int productPrice;

    private int quantity;

    private OrderItem(Long productId, String productTitle, int productPrice, int quantity) {
        this.productId = productId;
        this.productTitle = productTitle;
        this.productPrice = productPrice;
        this.quantity = quantity;
    }

    public static OrderItem of(Long productId, String productTitle, int productPrice, int quantity) {
        return new OrderItem(productId,
                productTitle,
                productPrice,
                quantity);
    }

    public int calculatePrice() {
    return productPrice * quantity;
    }

    public void assignOrder(Order order){
        this.order = order;
    }
}
