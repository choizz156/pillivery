package com.team33.modulecore.core.payment.domain.request;

public interface RequestService<T, R> {

	T request(R request);
}
