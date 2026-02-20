package org.example.domain.category.exception;

import org.example.global.response.exception.BaseException;
import org.example.global.response.exception.ErrorCode;

public class CategoryException extends BaseException {
    public CategoryException(ErrorCode errorCode) {
        super(errorCode);
    }
}
