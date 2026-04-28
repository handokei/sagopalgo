package org.example.domain.order.service;

import org.example.domain.order.controller.dto.OrderCreateRequestDto;
import org.example.domain.order.controller.dto.OrderCreateResponseDto;
import org.example.domain.order.controller.dto.OrderStatusResponseDto;

public interface OrderFacade {

    OrderCreateResponseDto createOrder(Long userId, OrderCreateRequestDto requestDto);

    OrderStatusResponseDto cancelOrder(Long userId, Long orderId);

    OrderStatusResponseDto shipOrder(Long userId, Long orderId, String trackingNumber);

    OrderStatusResponseDto completeOrder(Long userId, Long orderId);
}
