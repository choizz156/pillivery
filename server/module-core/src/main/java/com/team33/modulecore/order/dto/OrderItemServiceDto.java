package com.team33.modulecore.order.dto;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class OrderItemServiceDto {

	private long itemId;
	private int quantity;
	private int period;
	private boolean subscription;

	@Builder
	public OrderItemServiceDto(
		long itemId,
		int quantity,
		int period,
		boolean subscription
	) {
		this.itemId = itemId;
		this.quantity = quantity;
		this.period = period;
		this.subscription = subscription;
	}
}
