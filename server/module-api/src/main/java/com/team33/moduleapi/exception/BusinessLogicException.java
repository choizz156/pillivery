package com.team33.moduleapi.exception;

import lombok.Getter;

@Getter
public class BusinessLogicException extends RuntimeException {

    private ExceptionCode exceptionCode;

    public BusinessLogicException(ExceptionCode exceptionCode) {
        super(exceptionCode.getMessage());
        this.exceptionCode = exceptionCode;
    }

    public BusinessLogicException(String message) {
        super(message);
    }
}
