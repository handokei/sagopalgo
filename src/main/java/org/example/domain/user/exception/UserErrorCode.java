package org.example.domain.user.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.example.global.response.exception.ErrorCode;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum UserErrorCode implements ErrorCode{
    DUPLICATION_EMAIL_EXCEPTION(HttpStatus.CONFLICT, "중복되는 이메일입니다." ),
    INVALID_PASSWORD(HttpStatus.UNAUTHORIZED, "비밀번호가 일치하지 않습니다." ),
    USER_NOT_FOUND_EXCEPTION(HttpStatus.NOT_FOUND, "존재하지 않는 유저입니다." ),
    INVALID_EMAIL(HttpStatus.BAD_REQUEST, "이메일은 필수입니다." ),
    INVALID_NAME(HttpStatus.BAD_REQUEST, "이름은 필수입니다." ),
    INVALID_NICKNAME(HttpStatus.BAD_REQUEST, "닉네임은 필수입니다." ),
    INVALID_ROLE(HttpStatus.BAD_REQUEST, "역할은 필수입니다." ),
    INVALID_PROVIDER(HttpStatus.BAD_REQUEST, "OAuth2 provider 정보는 필수입니다."),
    INVALID_AUTH_CODE(HttpStatus.UNAUTHORIZED, "유효하지 않거나 만료된 인증 코드입니다.");


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
