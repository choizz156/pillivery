package com.team33.modulecore.core.payment.domain.approve;

import com.team33.modulecore.core.payment.domain.dto.ApproveRequest;

public interface ApproveFacade<T, S extends ApproveRequest> {

	T approveFirst(S approveRequest);
	T approveSubscription(Long orderId);

}
