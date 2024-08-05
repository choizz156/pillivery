package com.team33.modulecore.core.payment.application.request;

import com.team33.modulecore.core.order.domain.entity.Order;

public interface RequestService<T> {
	T request(Order order);
}
