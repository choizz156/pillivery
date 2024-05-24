package com.team33.modulecore.item.events;

import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.team33.modulecore.item.domain.repository.ItemCommandRepository;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class ItemSaleCountHandler {

	private final ItemCommandRepository itemCommandRepository;

	@Async
	@Transactional
	@EventListener
	public void handle(ItemSaleCountedEvent event) {
		event.getItemId().forEach(itemCommandRepository::incrementSales);
	}
}
