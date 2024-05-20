package com.team33.modulecore.payment.application.approve;

import com.team33.modulecore.order.domain.entity.Order;
import com.team33.modulecore.payment.dto.ApproveRequest;

public interface SubscriptionApproveService<T> {

	T approveFirstTime(ApproveRequest approveRequest);
	T approveSubscribe(Order order);
}
