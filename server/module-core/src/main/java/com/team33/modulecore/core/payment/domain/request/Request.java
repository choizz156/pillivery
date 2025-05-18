package com.team33.modulecore.core.payment.domain.request;

public interface Request<T, R> {
	T request(R request);
}
