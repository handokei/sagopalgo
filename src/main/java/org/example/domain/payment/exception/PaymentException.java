package org.example.domain.payment.exception;

import org.example.global.response.exception.BaseException;
import org.example.global.response.exception.ErrorCode;

public class PaymentException extends BaseException {

    public PaymentException(ErrorCode errorCode) {
        super(errorCode);
    }
}
