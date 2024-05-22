package com.team33.moduleapi.ui.payment.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
public class RefundDto {
	private int cancelAmount;
	private int cancelTaxFreeAmount;
	private int cancelAvailableAmount;
}
