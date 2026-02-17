package org.example.domain.order.controller.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.example.domain.order.domain.model.Order;
import org.example.domain.order.domain.model.OrderStatus;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class OrderResponseDto {

    private Long id;

    private String productTitle;

    private int totalPrice;

    private OrderStatus orderStatus;

    private String recipientName;

    private String phoneNumber;

    private String address;

    private LocalDateTime createAt;


    public static OrderResponseDto from(Order order) {
        return new OrderResponseDto(
                order.getId(),
                order.getSummaryTitle(),
                order.getTotalPrice(),
                order.getOrderStatus(),
                order.getRecipientName(),
                order.getPhoneNumber(),
                order.getAddress(),
                order.getCreatedAt()
        );
    }
}
