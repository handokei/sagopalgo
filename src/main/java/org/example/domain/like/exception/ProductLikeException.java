package org.example.domain.like.exception;

import lombok.Getter;
import org.example.global.response.exception.BaseException;
import org.example.global.response.exception.ErrorCode;

@Getter
public class ProductLikeException extends BaseException {
    public ProductLikeException(ErrorCode errorCode) {
        super(errorCode);
    }
}
