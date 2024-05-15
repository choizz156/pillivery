package com.team33.modulecore.payment.application.request;

public interface RequestFacade<T> {

	T request(long orderId);
}
