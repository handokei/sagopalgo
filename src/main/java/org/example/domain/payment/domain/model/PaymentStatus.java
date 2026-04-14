package org.example.domain.payment.domain.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum PaymentStatus {
    PENDING("결제대기"),
    PAID("결제완료"),
    FAILED("결제실패"),
    CANCELLED("결제취소");

    private final String description;
}
