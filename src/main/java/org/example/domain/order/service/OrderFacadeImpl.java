package org.example.domain.order.service;

import lombok.RequiredArgsConstructor;
import org.example.domain.cart.domain.model.OwnerType;
import org.example.domain.cart.service.CartService;
import org.example.domain.notification.domain.model.NotificationType;
import org.example.domain.notification.event.OrderNotificationEvent;
import org.example.domain.order.controller.dto.OrderCreateRequestDto;
import org.example.domain.order.controller.dto.OrderCreateResponseDto;
import org.example.domain.order.controller.dto.OrderStatusResponseDto;
import org.example.domain.order.domain.model.Order;
import org.example.domain.order.domain.model.OrderItem;
import org.example.domain.product.domain.model.Product;
import org.example.domain.product.domain.model.Product;
import org.example.domain.product.service.StockService;
import org.example.domain.user.service.UserService;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Component
@RequiredArgsConstructor
public class OrderFacadeImpl implements OrderFacade {

    private final OrderService orderService;
    private final UserService userService;
    private final StockService stockService;
    private final CartService cartService;
    private final ApplicationEventPublisher eventPublisher;

    @Override
    @Transactional
    public OrderCreateResponseDto createOrder(Long userId, OrderCreateRequestDto requestDto) {
        userService.validateUserExists(userId);

        List<OrderItem> orderItems = requestDto.getItems().stream()
                .map(itemDto -> {
                    Product product = stockService.decreaseStock(
                            itemDto.getProductId(),
                            itemDto.getQuantity()
                    );
                    return OrderItem.of(
                            itemDto.getProductId(),
                            product.getTitle(),
                            product.getPrice(),
                            itemDto.getQuantity()
                    );
                })
                .toList();

        Order order = orderService.createOrder(
                userId, orderItems,
                requestDto.getName(),
                requestDto.getPhoneNumber(),
                requestDto.getAddress()
        );

        cartService.clearCart(OwnerType.USER, userId.toString());

        eventPublisher.publishEvent(new OrderNotificationEvent(
                userId, NotificationType.ORDER_CREATED,
                NotificationType.ORDER_CREATED.getMessage() + ": " + order.getSummaryTitle(),
                order.getId()));

        return OrderCreateResponseDto.from(order);
    }

    @Override
    @Transactional
    public OrderStatusResponseDto cancelOrder(Long userId, Long orderId) {
        userService.validateUserExists(userId);

        OrderStatusResponseDto responseDto = orderService.cancelOrder(orderId);

        Order order = orderService.findOrder(orderId);
        for (OrderItem orderItem : order.getOrderItems()) {
            stockService.restoreStock(orderItem.getProductId(), orderItem.getQuantity());
        }

        eventPublisher.publishEvent(new OrderNotificationEvent(
                userId, NotificationType.ORDER_CANCELED,
                NotificationType.ORDER_CANCELED.getMessage() + ": " + order.getSummaryTitle(),
                order.getId()));

        return responseDto;
    }

    @Override
    @Transactional
    public OrderStatusResponseDto shipOrder(Long userId, Long orderId, String trackingNumber) {
        userService.validateUserExists(userId);

        OrderStatusResponseDto responseDto = orderService.shipOrder(orderId, trackingNumber);

        Order order = orderService.findOrder(orderId);
        eventPublisher.publishEvent(new OrderNotificationEvent(
                userId, NotificationType.ORDER_SHIPPED,
                NotificationType.ORDER_SHIPPED.getMessage() + ": " + order.getSummaryTitle(),
                order.getId()));

        return responseDto;
    }

    @Override
    @Transactional
    public OrderStatusResponseDto completeOrder(Long userId, Long orderId) {
        userService.validateUserExists(userId);

        OrderStatusResponseDto responseDto = orderService.completeOrder(orderId);

        Order order = orderService.findOrder(orderId);
        eventPublisher.publishEvent(new OrderNotificationEvent(
                userId, NotificationType.ORDER_COMPLETED,
                NotificationType.ORDER_COMPLETED.getMessage() + ": " + order.getSummaryTitle(),
                order.getId()));

        return responseDto;
    }
}
