package com.team33.moduleexternalapi.exception;

import lombok.Getter;

@Getter
public class SubscriptionPaymentException extends RuntimeException {

	private int statusCode;
	private String errorBody;

	public SubscriptionPaymentException(int statusCode, String message, String errorBody) {

		super(message);
		this.statusCode = statusCode;
		this.errorBody = errorBody;
	}

	public SubscriptionPaymentException(String message) {
		super(message);
	}

}
