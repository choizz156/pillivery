package com.team33.modulecore.payment.application.refund;

import lombok.Builder;
import lombok.Getter;

@Getter
public class RefundContext {
	private Long orderId;
	private int cancelAmount;
	private int cancelTaxFreeAmount;

	@Builder
	public RefundContext(Long orderId, int cancelAmount, int cancelTaxFreeAmount) {
		this.orderId = orderId;
		this.cancelAmount = cancelAmount;
		this.cancelTaxFreeAmount = cancelTaxFreeAmount;
	}
}
