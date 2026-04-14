package org.example.domain.order.service;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.domain.cart.domain.model.OwnerType;
import org.example.domain.cart.service.CartService;
import org.example.domain.delivery.domain.model.Delivery;
import org.example.domain.notification.domain.model.NotificationType;
import org.example.domain.notification.event.OrderNotificationEvent;
import org.example.domain.order.controller.dto.OrderCreateRequestDto;
import org.example.domain.order.controller.dto.OrderCreateResponseDto;
import org.example.domain.order.controller.dto.OrderResponseDto;
import org.example.domain.order.controller.dto.OrderStatusResponseDto;
import org.example.domain.order.domain.model.Order;
import org.example.domain.order.domain.model.OrderItem;
import org.example.domain.order.domain.repository.OrderRepository;
import org.example.domain.order.exception.OrderErrorCode;
import org.example.domain.order.exception.OrderException;
import org.example.domain.product.domain.model.Product;
import org.example.domain.product.domain.repository.ProductRepository;
import org.example.domain.product.service.StockService;
import org.example.domain.user.domain.repository.UserRepository;
import org.example.domain.user.exception.UserErrorCode;
import org.example.domain.user.exception.UserException;
import org.springframework.context.ApplicationEventPublisher;
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
    private final ProductRepository productRepository;
    private final UserRepository userRepository;
    private final ApplicationEventPublisher eventPublisher;
    private final CartService cartService;
    private final StockService stockService;

    @Transactional
    public OrderCreateResponseDto createOrder(Long userId, @Valid OrderCreateRequestDto requestDto) {
        userRepository.findByIdAndIsDeletedFalse(userId)
                .orElseThrow(() -> new UserException(UserErrorCode.USER_NOT_FOUND_EXCEPTION));

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

        Delivery delivery = Delivery.of(
                requestDto.getName(),
                requestDto.getPhoneNumber(),
                requestDto.getAddress()
        );

        Order order = Order.of(userId, orderItems, delivery);
        orderRepository.save(order);

        // 장바구니 비우기
        cartService.clearCart(OwnerType.USER, userId.toString());

        eventPublisher.publishEvent(new OrderNotificationEvent(userId, NotificationType.ORDER_CREATED,
                NotificationType.ORDER_CREATED.getMessage() + ": " + order.getSummaryTitle(), order.getId()));

        return OrderCreateResponseDto.from(order);
    }

    public Page<OrderResponseDto> getOrders(Long userId, int page, int size) {
        userRepository.findByIdAndIsDeletedFalse(userId)
                .orElseThrow(() -> new UserException(UserErrorCode.USER_NOT_FOUND_EXCEPTION));

        Pageable pageable = PageRequest.of(page, Math.min(size, MAX_PAGE_SIZE));

        Page<Order> orders = orderRepository.findByUserIdAndIsDeletedFalse(userId, pageable);

        return orders.map(OrderResponseDto::from);
    }

    public OrderResponseDto getOrder(Long userId, Long id) {
    userRepository.findByIdAndIsDeletedFalse(userId)
            .orElseThrow(() -> new UserException(UserErrorCode.USER_NOT_FOUND_EXCEPTION));

        Order order = orderRepository.findByIdAndIsDeletedFalse(id)
                .orElseThrow(() -> new OrderException(OrderErrorCode.ORDER_NOT_FOUND_EXCEPTION));

        return OrderResponseDto.from(order);

    }


    @Transactional
    public OrderStatusResponseDto cancelOrder(Long userId, Long id) {
        userRepository.findByIdAndIsDeletedFalse(userId)
                .orElseThrow(() -> new UserException(UserErrorCode.USER_NOT_FOUND_EXCEPTION));

        Order order = orderRepository.findByIdAndIsDeletedFalse(id)
                .orElseThrow(() -> new OrderException(OrderErrorCode.ORDER_NOT_FOUND_EXCEPTION));

        order.cancel();
        order.getDelivery().cancel();

        for (OrderItem orderItem : order.getOrderItems()) {
            stockService.restoreStock(orderItem.getProductId(), orderItem.getQuantity());
        }

        eventPublisher.publishEvent(new OrderNotificationEvent(userId, NotificationType.ORDER_CANCELED,
                NotificationType.ORDER_CANCELED.getMessage() + ": " + order.getSummaryTitle(), order.getId()));

        return OrderStatusResponseDto.from(order);
    }

    public Page<OrderResponseDto> getAllOrders(int page, int size) {
        Pageable pageable = PageRequest.of(page, Math.min(size, MAX_PAGE_SIZE));
        return orderRepository.findByIsDeletedFalse(pageable).map(OrderResponseDto::from);
    }

    @Transactional
    public OrderStatusResponseDto shipOrder(Long userId, Long id, String trackingNumber) {
    userRepository.findByIdAndIsDeletedFalse(userId)
            .orElseThrow(() -> new UserException(UserErrorCode.USER_NOT_FOUND_EXCEPTION));

        Order order = orderRepository.findByIdAndIsDeletedFalse(id)
                .orElseThrow(() -> new OrderException(OrderErrorCode.ORDER_NOT_FOUND_EXCEPTION));

        order.shipped();
        if (trackingNumber != null && !trackingNumber.isBlank()) {
            order.getDelivery().ship(trackingNumber);
        } else {
            order.getDelivery().ship();
        }

        eventPublisher.publishEvent(new OrderNotificationEvent(userId, NotificationType.ORDER_SHIPPED,
                NotificationType.ORDER_SHIPPED.getMessage() + ": " + order.getSummaryTitle(), order.getId()));

        return OrderStatusResponseDto.from(order);
    }

    @Transactional
    public OrderStatusResponseDto completeOrder(Long userId, Long id) {
        userRepository.findByIdAndIsDeletedFalse(userId)
                .orElseThrow(() -> new UserException(UserErrorCode.USER_NOT_FOUND_EXCEPTION));

        Order order = orderRepository.findByIdAndIsDeletedFalse(id)
                .orElseThrow(() -> new OrderException(OrderErrorCode.ORDER_NOT_FOUND_EXCEPTION));

        order.completed();
        order.getDelivery().complete();

        eventPublisher.publishEvent(new OrderNotificationEvent(userId, NotificationType.ORDER_COMPLETED,
                NotificationType.ORDER_COMPLETED.getMessage() + ": " + order.getSummaryTitle(), order.getId()));

        return OrderStatusResponseDto.from(order);
    }
}
