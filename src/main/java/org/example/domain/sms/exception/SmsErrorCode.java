package org.example.domain.sms.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.example.global.response.exception.ErrorCode;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum SmsErrorCode implements ErrorCode {
    INVALID_VERIFICATION_CODE(HttpStatus.BAD_REQUEST, "인증번호가 일치하지 않습니다."),
    EXPIRED_VERIFICATION_CODE(HttpStatus.BAD_REQUEST, "인증번호가 만료되었습니다."),
    PHONE_NOT_VERIFIED(HttpStatus.BAD_REQUEST, "휴대폰 인증이 필요합니다."),
    SMS_SEND_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "문자 발송에 실패했습니다.");

    private final HttpStatus status;
    private final String message;

    @Override
    public String getMessage() {
        return message;
    }

    @Override
    public HttpStatus getStatus() {
        return status;
    }
}
