package org.example.domain.order.controller.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.example.domain.delivery.domain.model.Delivery;
import org.example.domain.delivery.domain.model.DeliveryStatus;
import org.example.domain.order.domain.model.Order;
import org.example.domain.order.domain.model.OrderStatus;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class OrderResponseDto {

    private Long id;

    private Long userId;

    private String productTitle;

    private int totalPrice;

    private OrderStatus orderStatus;

    private String recipientName;

    private String phoneNumber;

    private String address;

    private DeliveryStatus deliveryStatus;

    private String trackingNumber;

    private LocalDateTime createAt;


    public static OrderResponseDto from(Order order) {
        Delivery delivery = order.getDelivery();
        return new OrderResponseDto(
                order.getId(),
                order.getUserId(),
                order.getSummaryTitle(),
                order.getTotalPrice(),
                order.getOrderStatus(),
                delivery != null ? delivery.getRecipientName() : null,
                delivery != null ? delivery.getPhoneNumber() : null,
                delivery != null ? delivery.getAddress() : null,
                delivery != null ? delivery.getStatus() : null,
                delivery != null ? delivery.getTrackingNumber() : null,
                order.getCreatedAt()
        );
    }
}
