package org.example.domain.order.exception;

import lombok.Getter;
import org.example.global.response.exception.BaseException;
import org.example.global.response.exception.ErrorCode;

@Getter
public class OrderException extends BaseException {

    public OrderException(ErrorCode errorCode) {
        super(errorCode);
    }
}
