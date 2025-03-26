package com.team33.modulecore.core.payment.domain.approve;

import com.team33.modulecore.core.payment.domain.dto.ApproveRequest;

public interface OneTimeApproveService<T> {

	T approveOneTime(ApproveRequest approveRequest);
}
