package org.example.global.response.exception;

import lombok.Getter;

@Getter
public class BaseException extends RuntimeException{
    private final ErrorCode errorCode;

    public BaseException(ErrorCode errorCode){
        super(errorCode.getMessage()); //메세지 전달
        this.errorCode = errorCode;
    }

}

