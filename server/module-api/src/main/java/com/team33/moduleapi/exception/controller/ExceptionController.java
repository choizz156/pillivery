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
import com.team33.modulecore.exception.DataSaveException;
import com.team33.moduleexternalapi.exception.ExternalApiException;
import com.team33.moduleexternalapi.exception.PaymentApiException;
import com.team33.moduleexternalapi.exception.SubscriptionPaymentException;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestControllerAdvice
public class ExceptionController {

	private static final Logger LOGGER = LoggerFactory.getLogger("fileLog");

	@ExceptionHandler(MethodArgumentNotValidException.class)
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	public ApiErrorResponse methodArgumentNotValidExceptionHandler(MethodArgumentNotValidException e) {

		LOGGER.debug("MethodArgumentNotValidException => {}", e.getBindingResult());
		return ApiErrorResponse.of(HttpStatus.BAD_REQUEST, e.getBindingResult());
	}

	@ExceptionHandler(ConstraintViolationException.class)
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	public ApiErrorResponse constraintViolationExceptionHandler(ConstraintViolationException e) {

		LOGGER.debug("ConstraintViolationException => {}", e.getMessage());
		return ApiErrorResponse.of(e);
	}

	@ExceptionHandler(BusinessLogicException.class)
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	public ApiErrorResponse businessLogicExceptionHandler(BusinessLogicException e) {
		LOGGER.info("BusinessLogicException => {}, class = {}", e.getMessage(), e.getStackTrace()[0]);
		return ApiErrorResponse.of(e.getMessage());
	}

	@ExceptionHandler(MethodArgumentTypeMismatchException.class)
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	public ApiErrorResponse methodArgumentTypeMismatchExceptionHandler(
		MethodArgumentTypeMismatchException e
	) {

		LOGGER.debug("methodArgumentTypeMismatchExceptionHandler => {}", e.getMessage());
		return ApiErrorResponse.of(HttpStatus.BAD_REQUEST, "잘못된 파라미터 타입입니다.");
	}

	@ExceptionHandler(MissingServletRequestParameterException.class)
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	public ApiErrorResponse missingServletRequestParameterExceptionHandler(
		MissingServletRequestParameterException e
	) {

		LOGGER.debug("MissingServletRequestParameterException => {}", e.getMessage());
		return ApiErrorResponse.of(e.getMessage());
	}

	@ExceptionHandler(IllegalArgumentException.class)
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	public ApiErrorResponse illegalArgumentExceptionHandler(IllegalArgumentException e) {

		LOGGER.info("illegalArgumentExceptionHandler => {}", e.getMessage());
		return ApiErrorResponse.of(e.getMessage());
	}

	@ExceptionHandler(DataSaveException.class)
	@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
	public ApiErrorResponse dataSaveExceptionHandler(DataSaveException e) {
		LOGGER.info("DataSaveException => {}", e.getMessage());
		return ApiErrorResponse.of(HttpStatus.INTERNAL_SERVER_ERROR, "알 수 없는 오류가 발생했습니다.");
	}

	@ExceptionHandler(SubscriptionPaymentException.class)
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	public ApiErrorResponse subscriptionPaymentExceptionHandler(SubscriptionPaymentException e) {

		LOGGER.error("SubscriptionPaymentException :: {}", e.getMessage());
		return ApiErrorResponse.of(HttpStatus.BAD_REQUEST, e.getErrorBody());
	}

	@ExceptionHandler(PaymentApiException.class)
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	public ApiErrorResponse paymentApiExceptionHandler(PaymentApiException e) {

		LOGGER.error("PaymentApiException :: {}", e.getMessage());
		return ApiErrorResponse.of(HttpStatus.BAD_REQUEST, e.getMessage());
	}

	@ExceptionHandler(ExternalApiException.class)
	@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
	public ApiErrorResponse externalApiExceptionHandler(ExternalApiException e) {

		LOGGER.error("ExternalApiException :: {}", e.getMessage());
		return ApiErrorResponse.of(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
	}

	@ExceptionHandler(RuntimeException.class)
	@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
	public ApiErrorResponse runtimeExceptionHandler(RuntimeException e) {

		Throwable rootCause = e;
		while (rootCause.getCause() != null) {
			rootCause = rootCause.getCause();
		}

		LOGGER.error("Caused by: {}: {}",
			rootCause.getClass().getSimpleName(),
			rootCause.getMessage()
		);

		StackTraceElement top = e.getStackTrace()[0];
		LOGGER.error("Exception at {}.{}({}:{}): {}",
			top.getClassName(),
			top.getMethodName(),
			top.getFileName(),
			top.getLineNumber(),
			e.getMessage()
		);

		return ApiErrorResponse.of(HttpStatus.INTERNAL_SERVER_ERROR, "알 수 없는 오류가 발생했습니다.");
	}

}



