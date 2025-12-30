package org.example.domain.cart.exception;

import org.example.global.response.exception.BaseException;
import org.example.global.response.exception.ErrorCode;

public class CartException extends BaseException {
    public CartException(ErrorCode errorCode) {
        super(errorCode);
    }
}
