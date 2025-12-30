package org.example.domain.cart.exception;

import org.example.global.response.exception.BaseException;
import org.example.global.response.exception.ErrorCode;

public class CartItemException extends BaseException {
    public CartItemException(ErrorCode errorCode) {
        super(errorCode);
    }
}
