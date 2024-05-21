package com.team33.moduleexternalapi.exception;

public  class PaymentApiException extends RuntimeException{
	public PaymentApiException(String message) {
		super(message);
	}

	public PaymentApiException(String message, Throwable cause) {
		super(message, cause);
	}
}
