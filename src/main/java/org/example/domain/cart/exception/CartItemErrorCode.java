package org.example.domain.cart.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.example.global.response.exception.ErrorCode;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum CartItemErrorCode implements ErrorCode {

    CART_ITEM_NOT_FOUND_EXCEPTION(HttpStatus.NOT_FOUND, "장바구니 아이템을 찾을 수 없습니다."),
    ZERO_QUANTITY_EXCEPTION(HttpStatus.BAD_REQUEST, "수량은 1개 이상이어야 합니다."),
    CART_ACCESS_DENIED(HttpStatus.FORBIDDEN, "계정 소유의 장바구니가 아닙니다."),
    INVALID_QUANTITY(HttpStatus.BAD_REQUEST, "유효한 수량이 아닙니다.");


    private final HttpStatus status;
    private final String message;


    @Override
    public HttpStatus getStatus() {
        return status;
    }

    @Override
    public String getMessage() {
        return message;
    }
}
