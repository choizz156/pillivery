package com.team33.modulecore.core.order.events;

import lombok.Getter;

@Getter
public class ItemOrderChangedPeriod{

	private final int period;
	private final long orderId;
	private final long itemOrderId;

	public ItemOrderChangedPeriod(int period, long orderId, long itemOrderId) {
		this.period = period;
		this.orderId = orderId;
		this.itemOrderId = itemOrderId;
	}
}
