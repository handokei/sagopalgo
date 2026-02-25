package org.example.domain.order.service;

import org.example.domain.order.controller.dto.OrderCreateRequestDto;
import org.example.domain.order.controller.dto.OrderCreateResponseDto;

public interface OrderFacade {

    OrderCreateResponseDto createOrder(Long userId, OrderCreateRequestDto requestDto);

}
