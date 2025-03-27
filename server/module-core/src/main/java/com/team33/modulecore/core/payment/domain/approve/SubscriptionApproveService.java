package com.team33.modulecore.core.payment.domain.approve;

import com.team33.modulecore.core.order.domain.entity.Order;
import com.team33.modulecore.core.payment.dto.ApproveRequest;

public interface SubscriptionApproveService<T> {

	T approveInitially(ApproveRequest approveRequest);
	T approveSubscribe(Order order);
}
