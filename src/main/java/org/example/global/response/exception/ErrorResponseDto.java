package org.example.global.response.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@AllArgsConstructor
@Getter
public class ErrorResponseDto {

    private final int status;
    private final String errorCode;
    private final String message;

    public ErrorResponseDto(ErrorCode errorCode, String message) {
        this.status = errorCode.getStatus().value();
        this.errorCode = errorCode.getStatus().name();
        this.message = message;
    }
    public ErrorResponseDto(HttpStatus httpStatus, String message){
        this.status = httpStatus.value();
        this.errorCode = httpStatus.getReasonPhrase();
        this.message = message;
    }
}

