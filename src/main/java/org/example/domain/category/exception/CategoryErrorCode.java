package org.example.domain.category.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.example.global.response.exception.ErrorCode;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum CategoryErrorCode implements ErrorCode {
    CATEGORY_NOT_FOUND(HttpStatus.NOT_FOUND, "카테고리를 찾을 수 없습니다."),
    CATEGORY_NAME_REQUIRED(HttpStatus.BAD_REQUEST, "카테고리명은 필수입니다."),
    DUPLICATE_CATEGORY_NAME(HttpStatus.BAD_REQUEST, "이미 존재하는 카테고리명입니다.");

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
