package org.example.domain.review.exception;

import org.example.global.response.exception.BaseException;
import org.example.global.response.exception.ErrorCode;

public class ReviewException extends BaseException {
    public ReviewException(ErrorCode errorCode) {
        super(errorCode);
    }
}
