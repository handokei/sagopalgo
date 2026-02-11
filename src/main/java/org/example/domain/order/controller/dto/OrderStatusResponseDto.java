package org.example.domain.order.controller.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.example.domain.order.domain.model.Order;
import org.example.domain.order.domain.model.OrderStatus;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class OrderStatusResponseDto {

    private Long id;

    private OrderStatus status;

    private LocalDateTime modifiedAt;

    public static OrderStatusResponseDto from(Order order) {
    return new OrderStatusResponseDto(order.getId(),
            order.getOrderStatus(),
            order.getUpdatedAt());
    }
}
