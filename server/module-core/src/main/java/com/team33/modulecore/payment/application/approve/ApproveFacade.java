package com.team33.modulecore.payment.application.approve;

import com.team33.modulecore.payment.dto.ApproveRequest;

public interface ApproveFacade<T, S extends ApproveRequest> {

	T approveFirst(S approveRequest);
	T approveSubscription(Long orderId);

}
