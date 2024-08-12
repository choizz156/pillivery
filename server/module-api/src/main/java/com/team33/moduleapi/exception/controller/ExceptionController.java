package com.team33.moduleapi.exception.controller;

import javax.validation.ConstraintViolationException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import com.team33.moduleapi.exception.dto.ErrorResponse;
import com.team33.modulecore.exception.BusinessLogicException;

@RestControllerAdvice
public class ExceptionController {

    private static final Logger log = LoggerFactory.getLogger("fileLog");

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse methodArgumentNotValidExceptionHandler(MethodArgumentNotValidException e) {
        log.warn("MethodArgumentNotValidException => {}", e.getBindingResult());
        return ErrorResponse.of(HttpStatus.BAD_REQUEST, e.getBindingResult());
    }

    @ExceptionHandler(ConstraintViolationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse constraintViolationExceptionHandler(ConstraintViolationException e) {
        log.warn("ConstraintViolationException => {}", e.getMessage());
        return ErrorResponse.of(e);
    }

    @ExceptionHandler(BusinessLogicException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse businessLogicExceptionHandler(BusinessLogicException e) {
        log.warn("BusinessLogicException => {}", e.getMessage());
        return ErrorResponse.of(e.getMessage());
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse methodArgumentTypeMismatchExceptionHandler(
        MethodArgumentTypeMismatchException e
    ) {
        log.warn("methodArgumentTypeMismatchExceptionHandler => {}", e.getMessage());
        return ErrorResponse.of(HttpStatus.BAD_REQUEST, e.getMessage());
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse missingServletRequestParameterExceptionHandler(
        MissingServletRequestParameterException e
    ) {
        log.warn("MissingServletRequestParameterException => {}", e.getMessage());
        return ErrorResponse.of(e.getMessage());
    }

    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse illegalArgumentExceptionHandler(IllegalArgumentException e) {
        log.warn("illegalArgumentExceptionHandler => {}", e.getMessage());
        return ErrorResponse.of(e.getMessage());
    }

    @ExceptionHandler(RuntimeException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse runtimeExceptionHandler(RuntimeException e) {
        log.error("runtime exception :: {}", e.getMessage());
        log.error("stack trace :: {}, {}",e.getStackTrace()[0].getClassName(), e.getStackTrace()[0].toString());
        return ErrorResponse.of("알 수 없는 오류가 발생했습니다.");
    }
}



