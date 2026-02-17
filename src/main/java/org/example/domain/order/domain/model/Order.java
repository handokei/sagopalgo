package org.example.domain.order.domain.model;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.example.domain.delivery.domain.model.Delivery;
import org.example.domain.order.exception.OrderErrorCode;
import org.example.domain.order.exception.OrderException;
import org.example.global.config.entity.BaseEntity;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "orders")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Order extends BaseEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private UUID orderNumber;

    private Long userId;

    @Enumerated(EnumType.STRING)
    private OrderStatus orderStatus;

    @OneToMany(mappedBy = "order", cascade = {CascadeType.PERSIST,CascadeType.REMOVE },
    orphanRemoval = true)
    private List<OrderItem> orderItems = new ArrayList<>();

    private int totalPrice;

    private String summaryTitle;

    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "delivery_id")
    private Delivery delivery;

    private  boolean isDeleted = false;

    private Order(Long userId, List<OrderItem> orderItems, Delivery delivery) {
        this.orderNumber = UUID.randomUUID();
        this.userId = userId;
        this.orderStatus = OrderStatus.CREATED;
        this.orderItems = orderItems;
        this.totalPrice = calculateTotalPrice();
        this.summaryTitle = generateSummaryTitle();
        this.delivery = delivery;

        orderItems.forEach(item -> item.assignOrder(this));
    }

    private String generateSummaryTitle() {
        int count = orderItems.size();
        String firstTitle = orderItems.stream()
                .map(OrderItem::getProductTitle)
                .findFirst()
                .orElse("");

        if (count <= 1) {
        return firstTitle;
    }
        return firstTitle + "외" + (count-1) + "개";
    }

    private int calculateTotalPrice() {
    return orderItems.stream()
            .mapToInt(OrderItem::calculatePrice)
            .sum();

    }

    public void delete(){
        this.isDeleted = true;
    }


    public static Order of(Long userId, List<OrderItem> orderItems, Delivery delivery){
             return new Order(userId, orderItems, delivery);
    }

    public void cancel() {
    if (orderStatus == OrderStatus.COMPLETED) {
        throw new OrderException(OrderErrorCode.ORDER_ALREADY_COMPLETED);
    }
    if(orderStatus == OrderStatus.CANCELED) {
        throw new OrderException(OrderErrorCode.ORDER_ALREADY_CANCELED);
    }
    this.orderStatus = OrderStatus.CANCELED;
    }

    public void pay() {
        if (orderStatus != OrderStatus.CREATED) {
            throw new OrderException(OrderErrorCode.ORDER_STATUS_NOT_CREATED);
        }
    this.orderStatus = OrderStatus.PAID;
    }

    public void shipped() {
        if (orderStatus != OrderStatus.PAID) {
            throw new OrderException(OrderErrorCode.ORDER_STATUS_NOT_PAID);
        }
        this.orderStatus = OrderStatus.SHIPPED;
    }

    public void completed() {
        if (orderStatus != OrderStatus.SHIPPED) {
            throw new OrderException(OrderErrorCode.ORDER_STATUS_NOT_SHIPPED);
        }
        this.orderStatus = OrderStatus.COMPLETED;
    }

}
