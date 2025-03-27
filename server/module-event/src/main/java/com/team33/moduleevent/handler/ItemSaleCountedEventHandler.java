package com.team33.moduleevent.handler;

import java.util.List;

import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import com.team33.modulecore.core.item.application.ItemCommandService;
import com.team33.modulecore.core.order.events.ItemSaleCountedEvent;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Component
public class ItemSaleCountedEventHandler {

	private final ItemCommandService itemCommandService;

	@Async
	@EventListener
	public void onApplicationEvent(ItemSaleCountedEvent event) {
		List<Long> itemIds = event.getItemId();
		itemCommandService.addSales(itemIds);
	}
}
