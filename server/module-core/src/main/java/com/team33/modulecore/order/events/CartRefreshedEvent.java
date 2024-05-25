package com.team33.modulecore.order.events;

import java.util.List;

import com.team33.modulecore.order.domain.entity.Order;

import lombok.Getter;

@Getter
public class CartRefreshedEvent {

	private final Order order;
	private final List<Long> orderedIds;

	public CartRefreshedEvent(Order order, List<Long> orderedIds) {
		this.order = order;
		this.orderedIds = orderedIds;
	}
}
