package com.team33.moduleapi.api.payment.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
public class RefundDto {
	private int cancelAmount;
	private int cancelTaxFreeAmount;
	private int cancelAvailableAmount;
}
