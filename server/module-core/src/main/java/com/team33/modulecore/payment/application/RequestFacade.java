package com.team33.modulecore.payment.application;

public interface RequestFacade<T> {

	T request(long orderId);
}
