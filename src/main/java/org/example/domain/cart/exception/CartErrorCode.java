package org.example.domain.cart.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.example.global.response.exception.ErrorCode;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum CartErrorCode implements ErrorCode{
    CART_NOT_FOUND_EXCEPTION(HttpStatus.UNAUTHORIZED,"장바구니를 찾을 수 없습니다" ),
    NOT_FOUND_GUEST_KEY_EXCEPTION(HttpStatus.UNAUTHORIZED,"guestKey를 찾을 수 없습니다." ),
    CART_ACCESS_DENIED(HttpStatus.UNAUTHORIZED, "올바른 장바구니 소유자가 아닙니다" );


    private final HttpStatus httpStatus;

    private final String message;


    @Override
    public HttpStatus getStatus() {
        return httpStatus;
    }
    @Override
    public String getMessage() {
        return message;
    }
}
