package com.team33.modulecore.payment.kakao.application.refund;

import lombok.Builder;
import lombok.Getter;

@Getter
public class RefundContext {

	private final int cancelAmount;
	private final int cancelTaxFreeAmount;

	@Builder
	public RefundContext( int cancelAmount, int cancelTaxFreeAmount) {
		this.cancelAmount = cancelAmount;
		this.cancelTaxFreeAmount = cancelTaxFreeAmount;
	}
}
