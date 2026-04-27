package org.example.domain.order.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.example.global.response.exception.ErrorCode;
import org.springframework.http.HttpStatus;

@RequiredArgsConstructor
@Getter
public enum OrderErrorCode implements ErrorCode {
    ORDER_NOT_FOUND_EXCEPTION(HttpStatus.NOT_FOUND, "존재하지 않는 주문입니다."),
    ORDER_STATUS_NOT_CREATED(HttpStatus.CONFLICT, "주문 상태가 생성상태가 아닙니다."),
    ORDER_ALREADY_COMPLETED(HttpStatus.CONFLICT, "주문완료 상태여서 취소가 불가능합니다."),
    ORDER_ALREADY_CANCELED(HttpStatus.CONFLICT, "이미 주문 취소한 주문입니다."),
    ORDER_STATUS_NOT_PAID(HttpStatus.CONFLICT, "주문 상태가 결제 완료 상태가 아닙니다."),
    ORDER_STATUS_NOT_SHIPPED(HttpStatus.CONFLICT, "주문 상태가 배송중이 아닙니다.");


private final HttpStatus status;
private final String message;
}
