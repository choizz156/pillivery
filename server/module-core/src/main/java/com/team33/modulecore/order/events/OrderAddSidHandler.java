package com.team33.modulecore.order.events;

import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.team33.modulecore.common.OrderFindHelper;
import com.team33.modulecore.order.domain.entity.Order;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class OrderAddSidHandler {

	private final OrderFindHelper orderFindHelper;

	@Async
	@Transactional
	@EventListener
	public void addSid(OrderAddedSidEvent event) {
		Order order = orderFindHelper.findOrder(event.getOrderId());
		order.addSid(event.getSid());
	}
}
