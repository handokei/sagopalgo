package org.example.domain.notification.exception;

import lombok.Getter;
import org.example.global.response.exception.BaseException;
import org.example.global.response.exception.ErrorCode;

@Getter
public class NotificationException extends BaseException {

    public NotificationException(ErrorCode errorCode) {
        super(errorCode);
    }
}
