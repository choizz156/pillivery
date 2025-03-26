package com.team33.modulecore.core.payment.domain.request;

public interface RequestFacade<T> {

	T request(long orderId);
}
