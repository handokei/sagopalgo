package org.example.domain.order.service;

import lombok.RequiredArgsConstructor;
import org.example.domain.order.controller.dto.OrderCreateResponseDto;
import org.example.domain.order.controller.dto.OrderResponseDto;
import org.example.domain.order.controller.dto.OrderStatusResponseDto;
import org.example.domain.order.domain.model.Order;
import org.example.domain.order.domain.model.OrderItem;
import org.example.domain.order.domain.repository.OrderRepository;
import org.example.domain.order.exception.OrderErrorCode;
import org.example.domain.order.exception.OrderException;
import org.example.domain.delivery.domain.model.Delivery;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class OrderService {

    private static final int MAX_PAGE_SIZE = 100;

    private final OrderRepository orderRepository;

    @Transactional
    public Order createOrder(Long userId, List<OrderItem> orderItems,
                             String name, String phoneNumber, String address) {
        Delivery delivery = Delivery.of(name, phoneNumber, address);
        Order order = Order.of(userId, orderItems, delivery);
        orderRepository.save(order);
        return order;
    }

    public Order findOrder(Long id) {
        return orderRepository.findByIdAndIsDeletedFalse(id)
                .orElseThrow(() -> new OrderException(OrderErrorCode.ORDER_NOT_FOUND_EXCEPTION));
    }

    public Page<OrderResponseDto> getOrders(Long userId, int page, int size) {
        Pageable pageable = PageRequest.of(page, Math.min(size, MAX_PAGE_SIZE));
        return orderRepository.findByUserIdAndIsDeletedFalse(userId, pageable)
                .map(OrderResponseDto::from);
    }

    public OrderResponseDto getOrder(Long userId, Long id) {
        Order order = findOrder(id);
        return OrderResponseDto.from(order);
    }

    @Transactional
    public OrderStatusResponseDto cancelOrder(Long id) {
        Order order = findOrder(id);
        order.cancel();
        order.getDelivery().cancel();
        return OrderStatusResponseDto.from(order);
    }

    public Page<OrderResponseDto> getAllOrders(int page, int size) {
        Pageable pageable = PageRequest.of(page, Math.min(size, MAX_PAGE_SIZE));
        return orderRepository.findByIsDeletedFalse(pageable).map(OrderResponseDto::from);
    }

    @Transactional
    public OrderStatusResponseDto shipOrder(Long id, String trackingNumber) {
        Order order = findOrder(id);
        order.shipped();
        if (trackingNumber != null && !trackingNumber.isBlank()) {
            order.getDelivery().ship(trackingNumber);
        } else {
            order.getDelivery().ship();
        }
        return OrderStatusResponseDto.from(order);
    }

    @Transactional
    public OrderStatusResponseDto completeOrder(Long id) {
        Order order = findOrder(id);
        order.completed();
        order.getDelivery().complete();
        return OrderStatusResponseDto.from(order);
    }

    @Transactional
    public void payOrder(Long orderId) {
        Order order = findOrder(orderId);
        order.pay();
        order.getDelivery().prepare();
    }
}
