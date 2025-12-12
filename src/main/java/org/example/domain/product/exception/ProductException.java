package org.example.domain.product.exception;

import org.example.global.response.exception.BaseException;
import org.example.global.response.exception.ErrorCode;

public class ProductException extends BaseException {
    public ProductException(ErrorCode errorCode) {
        super(errorCode);
    }
}
