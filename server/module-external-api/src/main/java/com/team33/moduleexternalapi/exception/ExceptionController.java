package com.team33.moduleexternalapi.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice(basePackages = "com.team33.moduleexternalapi")
public class ExceptionController {

	@ResponseStatus(HttpStatus.BAD_REQUEST)
	@ExceptionHandler(PaymentApiException.class)
	public ErrorResponse handleException(PaymentApiException e) {
		return ErrorResponse.of(e.getMessage());
	}
}
