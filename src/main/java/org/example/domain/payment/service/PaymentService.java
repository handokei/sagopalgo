package org.example.domain.payment.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.domain.notification.domain.model.NotificationType;
import org.example.domain.notification.event.OrderNotificationEvent;
import org.example.domain.order.domain.model.Order;
import org.example.domain.order.service.OrderService;
import org.example.domain.payment.client.PortOneClient;
import org.example.domain.payment.controller.dto.PaymentConfirmRequestDto;
import org.example.domain.payment.controller.dto.PaymentPrepareRequestDto;
import org.example.domain.payment.controller.dto.PaymentResponseDto;
import org.example.domain.payment.domain.model.Payment;
import org.example.domain.payment.domain.model.PaymentStatus;
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
    private final OrderService orderService;
    private final PortOneClient portOneClient;
    private final ApplicationEventPublisher eventPublisher;

    @Transactional
    public PaymentResponseDto prepare(Long userId, PaymentPrepareRequestDto requestDto) {
        Order order = orderService.findOrder(requestDto.getOrderId());

        paymentRepository.findByOrderId(order.getId()).ifPresent(p -> {
            if (p.getStatus() == PaymentStatus.PAID) {
                throw new PaymentException(PaymentErrorCode.PAYMENT_ALREADY_PAID);
            }
            paymentRepository.delete(p);
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

        if (!payment.getUserId().equals(userId)) {
            throw new PaymentException(PaymentErrorCode.PAYMENT_FORBIDDEN);
        }

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

        orderService.payOrder(payment.getOrderId());

        Order order = orderService.findOrder(payment.getOrderId());
        eventPublisher.publishEvent(new OrderNotificationEvent(userId, NotificationType.ORDER_PAID,
                NotificationType.ORDER_PAID.getMessage() + ": " + order.getSummaryTitle(), order.getId()));

        log.info("결제 확인 완료 - paymentId: {}, orderId: {}", payment.getId(), order.getId());

        return PaymentResponseDto.from(payment);
    }
}
