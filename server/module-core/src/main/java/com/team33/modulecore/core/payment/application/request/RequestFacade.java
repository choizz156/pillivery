package com.team33.modulecore.core.payment.application.request;

public interface RequestFacade<T> {

	T request(long orderId);
}
