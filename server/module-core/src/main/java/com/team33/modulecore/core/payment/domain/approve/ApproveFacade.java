package com.team33.modulecore.core.payment.domain.approve;

import com.team33.modulecore.core.payment.dto.ApproveRequest;

public interface ApproveFacade<T, S extends ApproveRequest> {

	T approveInitially(S approveRequest);
	T approveSubscription(Long orderId);

}
