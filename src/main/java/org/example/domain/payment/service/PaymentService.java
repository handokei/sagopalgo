package org.example.domain.payment.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.domain.notification.domain.model.NotificationType;
import org.example.domain.notification.event.OrderNotificationEvent;
import org.example.domain.order.domain.model.Order;
import org.example.domain.order.domain.repository.OrderRepository;
import org.example.domain.order.exception.OrderErrorCode;
import org.example.domain.order.exception.OrderException;
import org.example.domain.payment.client.PortOneClient;
import org.example.domain.payment.controller.dto.PaymentConfirmRequestDto;
import org.example.domain.payment.controller.dto.PaymentPrepareRequestDto;
import org.example.domain.payment.controller.dto.PaymentResponseDto;
import org.example.domain.payment.domain.model.Payment;
import org.example.domain.payment.domain.repository.PaymentRepository;
import org.example.domain.payment.exception.PaymentErrorCode;
import org.example.domain.payment.exception.PaymentException;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final OrderRepository orderRepository;
    private final PortOneClient portOneClient;
    private final ApplicationEventPublisher eventPublisher;

    @Transactional
    public PaymentResponseDto prepare(Long userId, PaymentPrepareRequestDto requestDto) {
        Order order = orderRepository.findByIdAndIsDeletedFalse(requestDto.getOrderId())
                .orElseThrow(() -> new OrderException(OrderErrorCode.ORDER_NOT_FOUND_EXCEPTION));

        paymentRepository.findByOrderId(order.getId()).ifPresent(p -> {
            throw new PaymentException(PaymentErrorCode.PAYMENT_ALREADY_PAID);
        });

        Payment payment = Payment.of(order.getId(), userId, order.getTotalPrice());
        paymentRepository.save(payment);

        log.info("결제 준비 완료 - paymentId: {}, orderId: {}, amount: {}",
                payment.getId(), order.getId(), order.getTotalPrice());

        return PaymentResponseDto.from(payment);
    }

    @Transactional
    public PaymentResponseDto confirm(Long userId, PaymentConfirmRequestDto requestDto) {
        Payment payment = paymentRepository.findById(requestDto.getPaymentId())
                .orElseThrow(() -> new PaymentException(PaymentErrorCode.PAYMENT_NOT_FOUND));

        PortOneClient.PortOnePaymentResponse portOneResponse =
                portOneClient.getPayment(requestDto.getPortOnePaymentId());

        if (portOneResponse.getAmount().getTotal() != payment.getAmount()) {
            payment.fail();
            throw new PaymentException(PaymentErrorCode.PAYMENT_AMOUNT_MISMATCH);
        }

        if (!"PAID".equals(portOneResponse.getStatus())) {
            payment.fail();
            throw new PaymentException(PaymentErrorCode.PORTONE_VERIFY_FAILED);
        }

        payment.confirm(requestDto.getPortOnePaymentId());

        Order order = orderRepository.findByIdAndIsDeletedFalse(payment.getOrderId())
                .orElseThrow(() -> new OrderException(OrderErrorCode.ORDER_NOT_FOUND_EXCEPTION));
        order.pay();
        order.getDelivery().prepare();

        eventPublisher.publishEvent(new OrderNotificationEvent(userId, NotificationType.ORDER_PAID,
                NotificationType.ORDER_PAID.getMessage() + ": " + order.getSummaryTitle(), order.getId()));

        log.info("결제 확인 완료 - paymentId: {}, orderId: {}", payment.getId(), order.getId());

        return PaymentResponseDto.from(payment);
    }
}
