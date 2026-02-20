package org.example.domain.review.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.example.global.response.exception.ErrorCode;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ReviewErrorCode implements ErrorCode {
    REVIEW_NOT_FOUND(HttpStatus.NOT_FOUND, "리뷰를 찾을 수 없습니다."),
    ALREADY_REVIEWED(HttpStatus.BAD_REQUEST, "이미 리뷰를 작성한 상품입니다."),
    INVALID_RATING(HttpStatus.BAD_REQUEST, "별점은 1~5 사이여야 합니다."),
    NOT_REVIEW_OWNER(HttpStatus.FORBIDDEN, "본인의 리뷰만 수정/삭제할 수 있습니다.");

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
