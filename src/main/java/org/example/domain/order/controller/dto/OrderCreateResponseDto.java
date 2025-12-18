package org.example.domain.order.controller.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.example.domain.order.domain.model.Order;
import org.example.domain.order.domain.model.OrderStatus;

@Getter
@AllArgsConstructor
public class OrderCreateResponseDto {

    private Long orderId;

    private String orderNumber;

    private int totalPrice;

    private OrderStatus orderStatus;


    public static OrderCreateResponseDto from(Order order) {
    return new OrderCreateResponseDto(
            order.getId(),
            order.getOrderNumber().toString(),
            order.getTotalPrice(),
            order.getOrderStatus());
    }
}
