package org.example.domain.payment.controller.dto;

import lombok.Getter;
import org.example.domain.payment.domain.model.Payment;
import org.example.domain.payment.domain.model.PaymentStatus;

import java.time.LocalDateTime;

@Getter
public class PaymentResponseDto {

    private final Long paymentId;
    private final Long orderId;
    private final int amount;
    private final PaymentStatus status;
    private final LocalDateTime paidAt;

    private PaymentResponseDto(Payment payment) {
        this.paymentId = payment.getId();
        this.orderId = payment.getOrderId();
        this.amount = payment.getAmount();
        this.status = payment.getStatus();
        this.paidAt = payment.getPaidAt();
    }

    public static PaymentResponseDto from(Payment payment) {
        return new PaymentResponseDto(payment);
    }
}
