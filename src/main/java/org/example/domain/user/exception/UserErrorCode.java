package org.example.domain.user.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.example.global.response.exception.ErrorCode;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum UserErrorCode implements ErrorCode{
    DUPLICATION_EMAIL_EXCEPTION(HttpStatus.UNAUTHORIZED,"중복되는 이메일입니다." ),
    INVALID_PASSWORD(HttpStatus.UNAUTHORIZED, "비빌번호가 일치하지 않습니다." ),
    USER_NOT_FOUND_EXCEPTION(HttpStatus.UNAUTHORIZED, "존재하지 않는 유저입니다."  );


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
