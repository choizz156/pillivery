package com.team33.modulecore.order.events;

import java.util.List;

import lombok.Getter;

@Getter
public class ItemSaleCountedEvent {

	private final List<Long> itemId;

	public ItemSaleCountedEvent(List<Long> itemId) {
		this.itemId = itemId;
	}
}
