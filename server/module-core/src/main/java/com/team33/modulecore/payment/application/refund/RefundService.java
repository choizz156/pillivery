package com.team33.modulecore.payment.application.refund;

public interface RefundService<T> {

	T refund(RefundContext refundContext);
}
