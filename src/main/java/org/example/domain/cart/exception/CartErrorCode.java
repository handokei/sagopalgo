package org.example.domain.cart.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.example.global.response.exception.ErrorCode;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum CartErrorCode implements ErrorCode{
    CART_NOT_FOUND_EXCEPTION(HttpStatus.NOT_FOUND, "장바구니를 찾을 수 없습니다."),
    NOT_FOUND_GUEST_KEY_EXCEPTION(HttpStatus.NOT_FOUND, "guestKey를 찾을 수 없습니다."),
    CART_ACCESS_DENIED(HttpStatus.FORBIDDEN, "올바른 장바구니 소유자가 아닙니다."),
    NOT_SALE_PRODUCT_QUANTITY_CHANGE_EXCEPTION(HttpStatus.BAD_REQUEST, "판매중인 상품이 아닙니다."),
    NOT_SALE_PRODUCT_INVALID_ADD_CART_EXCEPTION(HttpStatus.BAD_REQUEST, "판매중인 상품만 장바구니에 담을 수 있습니다."),
    OWN_PRODUCT_CART_EXCEPTION(HttpStatus.BAD_REQUEST, "본인 상품은 장바구니에 담을 수 없습니다.");


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
