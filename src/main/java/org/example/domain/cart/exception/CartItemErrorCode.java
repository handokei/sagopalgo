package org.example.domain.cart.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.example.global.response.exception.ErrorCode;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum CartItemErrorCode implements ErrorCode {

    CART_ITEM_NOT_FOUND_EXCEPTION(HttpStatus.UNAUTHORIZED, "장바구니 아이템을 찾을 수 없습니다.");


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
