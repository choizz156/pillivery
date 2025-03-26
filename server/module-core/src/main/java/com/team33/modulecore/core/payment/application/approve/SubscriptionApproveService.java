package com.team33.modulecore.core.payment.application.approve;

import com.team33.modulecore.core.order.domain.entity.Order;
import com.team33.modulecore.core.payment.dto.ApproveRequest;

public interface SubscriptionApproveService<T> {

	T approveInitial(ApproveRequest approveRequest);
	T approveSubscribe(Order order);
}
