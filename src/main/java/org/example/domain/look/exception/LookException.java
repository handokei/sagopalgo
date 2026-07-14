package org.example.domain.look.exception;

import org.example.global.response.exception.BaseException;
import org.example.global.response.exception.ErrorCode;

public class LookException extends BaseException {
    public LookException(ErrorCode errorCode) {
        super(errorCode);
    }
}
