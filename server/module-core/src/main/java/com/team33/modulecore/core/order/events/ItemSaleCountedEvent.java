package com.team33.modulecore.core.order.events;

import java.util.List;

import lombok.Getter;

@Getter
public class ItemSaleCountedEvent {

	private final List<Long> itemId;

	public ItemSaleCountedEvent(List<Long> itemId) {
		this.itemId = itemId;
	}
}
