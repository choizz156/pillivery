package com.team33.modulecore.payment.application.approve;

import com.team33.modulecore.payment.dto.ApproveRequest;

public interface SubscriptionApproveService<T> {

	T approveFirstTime(Long orderId, ApproveRequest approveRequest);

}
