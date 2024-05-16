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
	private boolean isSubscription;

	@Builder
	public OrderItemServiceDto(
		long itemId,
		int quantity,
		int period,
		boolean isSubscription
	) {
		this.itemId = itemId;
		this.quantity = quantity;
		this.period = period;
		this.isSubscription = isSubscription;
	}
}
