package org.example.domain.order.service;

import lombok.RequiredArgsConstructor;
import org.example.domain.cart.domain.model.OwnerType;
import org.example.domain.cart.service.CartService;
import org.example.domain.notification.domain.model.NotificationType;
import org.example.domain.notification.event.OrderNotificationEvent;
import org.example.domain.order.controller.dto.OrderCreateRequestDto;
import org.example.domain.order.controller.dto.OrderCreateResponseDto;
import org.example.domain.order.domain.model.Order;
import org.example.domain.order.domain.model.OrderItem;
import org.example.domain.product.domain.model.Product;
import org.example.domain.product.service.StockService;
import org.example.domain.user.service.UserService;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 주문 생성의 <b>트랜잭션 경계</b>를 담당한다.
 *
 * <p>분산락 획득/해제는 {@link OrderFacadeImpl}이 담당하고, 이 서비스는 락 안쪽에서
 * 단일 트랜잭션으로 실행된다. 재고 차감 · 주문 생성 · 장바구니 정리 · 이벤트 발행이
 * 하나의 트랜잭션으로 원자적으로 처리되며, 메서드 반환 시점에 커밋된다.
 * 락은 이 커밋 이후 Facade에서 해제되므로 "커밋 이후 락 해제" 순서가 구조적으로 보장된다.
 */
@Service
@RequiredArgsConstructor
public class OrderTransactionService {

    private final OrderService orderService;
    private final StockService stockService;
    private final CartService cartService;
    private final UserService userService;
    private final ApplicationEventPublisher eventPublisher;

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
}
