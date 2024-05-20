package com.team33.modulecore.payment.application.approve;

import com.team33.modulecore.payment.dto.ApproveRequest;

public interface NormalApproveService<T> {

	T approveOneTime(ApproveRequest approveRequest);
}
