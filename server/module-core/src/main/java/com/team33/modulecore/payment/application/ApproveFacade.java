package com.team33.modulecore.payment.application;

import com.team33.modulecore.payment.dto.ApproveRequest;

public interface ApproveFacade<T, S extends ApproveRequest> {

	T approve(S approveRequest);

}
