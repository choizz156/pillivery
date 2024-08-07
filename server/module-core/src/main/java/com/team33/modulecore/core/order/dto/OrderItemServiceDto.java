package com.team33.modulecore.core.order.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
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
