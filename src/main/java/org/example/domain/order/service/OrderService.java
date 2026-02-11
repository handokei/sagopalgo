package org.example.domain.order.service;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.domain.notification.domain.model.NotificationType;
import org.example.domain.notification.service.NotificationService;
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
import org.example.domain.product.exception.ProductErrorCode;
import org.example.domain.product.exception.ProductException;
import org.example.domain.user.domain.repository.UserRepository;
import org.example.domain.user.exception.UserErrorCode;
import org.example.domain.user.exception.UserException;
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

    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;
    private final NotificationService notificationService;

    @Transactional
    public OrderCreateResponseDto createOrder(Long userId, @Valid OrderCreateRequestDto requestDto) {
        userRepository.findByIdAndIsDeletedFalse(userId)
                .orElseThrow(() -> new UserException(UserErrorCode.USER_NOT_FOUND_EXCEPTION));

        Product product = productRepository.findByIdWithLock(requestDto.getProductId())
                .orElseThrow(() -> new ProductException(ProductErrorCode.PRODUCT_NOT_FOUND_EXCEPTION));

        product.decreaseStock(requestDto.getQuantity());

        OrderItem orderItem = OrderItem.of(requestDto.getProductId(),
                product.getTitle(),
                product.getPrice(),
                requestDto.getQuantity());

        Order order = Order.of(userId, List.of(orderItem));
        orderRepository.save(order);

        notificationService.send(userId, NotificationType.ORDER_CREATED,
                "주문이 생성되었습니다: " + order.getSummaryTitle(), order.getId());

        return OrderCreateResponseDto.from(order);
    }

    public Page<OrderResponseDto> getOrders(Long userId, int page, int size) {
        userRepository.findByIdAndIsDeletedFalse(userId)
                .orElseThrow(() -> new UserException(UserErrorCode.USER_NOT_FOUND_EXCEPTION));

        Pageable pageable = PageRequest.of(page, size);

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
    public OrderStatusResponseDto payOrder(Long userId, Long id) {
    userRepository.findByIdAndIsDeletedFalse(userId)
            .orElseThrow(() -> new UserException(UserErrorCode.USER_NOT_FOUND_EXCEPTION));

        Order order = orderRepository.findByIdAndIsDeletedFalse(id)
                .orElseThrow(() -> new OrderException(OrderErrorCode.ORDER_NOT_FOUND_EXCEPTION));

        order.pay();

        notificationService.send(userId, NotificationType.ORDER_PAID,
                "결제가 완료되었습니다: " + order.getSummaryTitle(), order.getId());

        return OrderStatusResponseDto.from(order);
    }

    @Transactional
    public OrderStatusResponseDto cancelOrder(Long userId, Long id) {
        userRepository.findByIdAndIsDeletedFalse(userId)
                .orElseThrow(() -> new UserException(UserErrorCode.USER_NOT_FOUND_EXCEPTION));

        Order order = orderRepository.findByIdAndIsDeletedFalse(id)
                .orElseThrow(() -> new OrderException(OrderErrorCode.ORDER_NOT_FOUND_EXCEPTION));

        order.cancel();

        for (OrderItem orderItem : order.getOrderItems()) {
            Product product = productRepository.findByIdWithLock(orderItem.getProductId())
                    .orElseThrow(() -> new ProductException(ProductErrorCode.PRODUCT_NOT_FOUND_EXCEPTION));
            product.restoreStock(orderItem.getQuantity());
        }

        notificationService.send(userId, NotificationType.ORDER_CANCELED,
                "주문이 취소되었습니다: " + order.getSummaryTitle(), order.getId());

        return OrderStatusResponseDto.from(order);
    }

    @Transactional
    public OrderStatusResponseDto shipOrder(Long userId, Long id) {
    userRepository.findByIdAndIsDeletedFalse(userId)
            .orElseThrow(() -> new UserException(UserErrorCode.USER_NOT_FOUND_EXCEPTION));

        Order order = orderRepository.findByIdAndIsDeletedFalse(id)
                .orElseThrow(() -> new OrderException(OrderErrorCode.ORDER_NOT_FOUND_EXCEPTION));

        order.shipped();

        notificationService.send(userId, NotificationType.ORDER_SHIPPED,
                "배송이 시작되었습니다: " + order.getSummaryTitle(), order.getId());

        return OrderStatusResponseDto.from(order);
    }

    @Transactional
    public OrderStatusResponseDto completeOrder(Long userId, Long id) {
        userRepository.findByIdAndIsDeletedFalse(userId)
                .orElseThrow(() -> new UserException(UserErrorCode.USER_NOT_FOUND_EXCEPTION));

        Order order = orderRepository.findByIdAndIsDeletedFalse(id)
                .orElseThrow(() -> new OrderException(OrderErrorCode.ORDER_NOT_FOUND_EXCEPTION));

        order.completed();

        notificationService.send(userId, NotificationType.ORDER_COMPLETED,
                "배송이 완료되었습니다: " + order.getSummaryTitle(), order.getId());

        return OrderStatusResponseDto.from(order);
    }
}
