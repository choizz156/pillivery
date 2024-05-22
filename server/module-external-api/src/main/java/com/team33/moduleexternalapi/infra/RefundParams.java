package com.team33.moduleexternalapi.infra;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class RefundParams {

	private String tid;
	private String cid;
	private Integer cancel_amount;
	private Integer cancel_tax_free_amount;

	@Builder
	public RefundParams(String tid, String cid, int cancelAmount, int cancelTaxFreeAmount) {
		this.tid = tid;
		this.cid = cid;
		this.cancel_amount = cancelAmount;
		this.cancel_tax_free_amount = cancelTaxFreeAmount;
	}
}
