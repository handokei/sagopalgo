package org.example.domain.like.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.example.global.response.exception.ErrorCode;
import org.springframework.http.HttpStatus;


@Getter
@RequiredArgsConstructor
public enum ProductLikeErrorCode implements ErrorCode {
    ;


    private final HttpStatus status;

    private final String message;
    @Override
    public HttpStatus getStatus() {
        return status;
    }

    @Override
    public String getMessage() {
        return message;
    }
}
