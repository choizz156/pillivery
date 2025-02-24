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

import com.team33.moduleapi.response.ApiErrorResponse;
import com.team33.modulecore.exception.BusinessLogicException;

@RestControllerAdvice
public class ExceptionController {

	private static final Logger log = LoggerFactory.getLogger("fileLog");

	@ExceptionHandler(MethodArgumentNotValidException.class)
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	public ApiErrorResponse methodArgumentNotValidExceptionHandler(MethodArgumentNotValidException e) {
		log.debug("MethodArgumentNotValidException => {}", e.getBindingResult());
		return ApiErrorResponse.of(HttpStatus.BAD_REQUEST, e.getBindingResult());
	}

	@ExceptionHandler(ConstraintViolationException.class)
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	public ApiErrorResponse constraintViolationExceptionHandler(ConstraintViolationException e) {
		log.debug("ConstraintViolationException => {}", e.getMessage());
		return ApiErrorResponse.of(e);
	}

	@ExceptionHandler(BusinessLogicException.class)
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	public ApiErrorResponse businessLogicExceptionHandler(BusinessLogicException e) {
		log.info("BusinessLogicException => {}", e.getMessage());
		return ApiErrorResponse.of(e.getMessage());
	}

	@ExceptionHandler(MethodArgumentTypeMismatchException.class)
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	public ApiErrorResponse methodArgumentTypeMismatchExceptionHandler(
		MethodArgumentTypeMismatchException e
	) {
		log.debug("methodArgumentTypeMismatchExceptionHandler => {}", e.getMessage());
		return ApiErrorResponse.of(HttpStatus.BAD_REQUEST, e.getMessage());
	}

	@ExceptionHandler(MissingServletRequestParameterException.class)
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	public ApiErrorResponse missingServletRequestParameterExceptionHandler(
		MissingServletRequestParameterException e
	) {
		log.debug("MissingServletRequestParameterException => {}", e.getMessage());
		return ApiErrorResponse.of(e.getMessage());
	}

	@ExceptionHandler(IllegalArgumentException.class)
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	public ApiErrorResponse illegalArgumentExceptionHandler(IllegalArgumentException e) {
		log.debug("illegalArgumentExceptionHandler => {}", e.getMessage());
		return ApiErrorResponse.of(e.getMessage());
	}

	@ExceptionHandler(RuntimeException.class)
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	public ApiErrorResponse runtimeExceptionHandler(RuntimeException e) {
		log.error("runtime exception :: {}", e.getLocalizedMessage());
		log.error("stack trace :: {}, {}", e.getStackTrace()[0].getClassName(), e.getStackTrace()[0]);
		return ApiErrorResponse.of("알 수 없는 오류가 발생했습니다.");
	}
}



