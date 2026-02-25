package org.example.domain.order.service;

import lombok.RequiredArgsConstructor;
import org.example.domain.order.controller.dto.OrderCreateRequestDto;
import org.example.domain.order.controller.dto.OrderCreateResponseDto;
import org.example.global.lock.DistributedLock;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class OrderFacadeImpl implements OrderFacade {

    private final OrderService orderService;

    @Override
    @DistributedLock(key = "'product:' + #requestDto.items[0].productId")
    public OrderCreateResponseDto createOrder(Long userId, OrderCreateRequestDto requestDto) {
        return orderService.createOrder(userId, requestDto);
    }
}
