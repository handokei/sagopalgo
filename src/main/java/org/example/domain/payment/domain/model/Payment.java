package org.example.domain.payment.domain.model;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.example.global.config.entity.BaseEntity;

import java.time.LocalDateTime;

@Getter
@Entity
@Table(name = "payments")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Payment extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long orderId;

    @Column(nullable = false)
    private Long userId;

    @Column(nullable = false)
    private int amount;

    private String portOnePaymentId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PaymentStatus status;

    private LocalDateTime paidAt;

    public static Payment of(Long orderId, Long userId, int amount) {
        Payment payment = new Payment();
        payment.orderId = orderId;
        payment.userId = userId;
        payment.amount = amount;
        payment.status = PaymentStatus.PENDING;
        return payment;
    }

    public void confirm(String portOnePaymentId) {
        this.portOnePaymentId = portOnePaymentId;
        this.status = PaymentStatus.PAID;
        this.paidAt = LocalDateTime.now();
    }

    public void fail() {
        this.status = PaymentStatus.FAILED;
    }
}
