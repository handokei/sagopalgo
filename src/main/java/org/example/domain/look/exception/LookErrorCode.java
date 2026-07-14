package org.example.domain.look.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.example.global.response.exception.ErrorCode;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum LookErrorCode implements ErrorCode {
    LOOK_NOT_FOUND(HttpStatus.NOT_FOUND, "룩을 찾을 수 없습니다."),
    LOOK_TITLE_REQUIRED(HttpStatus.BAD_REQUEST, "룩 제목은 필수입니다."),
    LOOK_HERO_IMAGE_REQUIRED(HttpStatus.BAD_REQUEST, "룩 대표 이미지는 필수입니다.");

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
