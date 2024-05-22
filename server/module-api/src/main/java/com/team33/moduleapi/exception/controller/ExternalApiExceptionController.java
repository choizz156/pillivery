package com.team33.moduleapi.exception.controller;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.team33.moduleapi.exception.dto.ErrorResponse;
import com.team33.moduleexternalapi.exception.PaymentApiException;

@RestControllerAdvice
public class ExternalApiExceptionController {

	@ResponseStatus(HttpStatus.BAD_REQUEST)
	@ExceptionHandler(PaymentApiException.class)
	public ErrorResponse handleException(PaymentApiException e) {
		return ErrorResponse.of(e.getMessage());
	}
}
