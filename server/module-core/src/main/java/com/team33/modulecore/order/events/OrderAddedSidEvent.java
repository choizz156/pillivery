package com.team33.modulecore.order.events;

import lombok.Getter;

@Getter
public class OrderAddedSidEvent {

	private final String sid;
	private final Long orderId;

	public OrderAddedSidEvent(String sid, Long orderId) {
		this.sid = sid;
		this.orderId = orderId;
	}
}
