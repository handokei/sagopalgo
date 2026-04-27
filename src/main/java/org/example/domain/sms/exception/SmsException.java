package org.example.domain.sms.exception;

import org.example.global.response.exception.BaseException;
import org.example.global.response.exception.ErrorCode;

public class SmsException extends BaseException {
    public SmsException(ErrorCode errorCode) {
        super(errorCode);
    }
}
