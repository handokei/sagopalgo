package org.example.domain.payment.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.example.global.response.exception.ErrorCode;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum PaymentErrorCode implements ErrorCode {

    PAYMENT_NOT_FOUND(HttpStatus.NOT_FOUND, "결제 정보를 찾을 수 없습니다."),
    PAYMENT_ALREADY_PAID(HttpStatus.CONFLICT, "이미 결제 완료된 주문입니다."),
    PAYMENT_FORBIDDEN(HttpStatus.FORBIDDEN, "해당 결제에 대한 권한이 없습니다."),
    PAYMENT_AMOUNT_MISMATCH(HttpStatus.BAD_REQUEST, "결제 금액이 주문 금액과 일치하지 않습니다."),
    PORTONE_VERIFY_FAILED(HttpStatus.BAD_GATEWAY, "PortOne 결제 검증에 실패했습니다.");

    private final HttpStatus status;
    private final String message;
}
