package com.team33.modulecore.core.payment.domain.request;

import com.team33.modulecore.core.order.domain.entity.Order;

public interface Request<T, R> {
	T request(R request);
}
