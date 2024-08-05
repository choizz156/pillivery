package com.team33.modulecore.core.item.application;

import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.event.TransactionalEventListener;

import com.team33.modulecore.core.item.domain.repository.ItemCommandRepository;
import com.team33.modulecore.core.order.events.ItemSaleCountedEvent;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class ItemSaleCountEventHandler {

	private final ItemCommandRepository itemCommandRepository;

	@Async
	@Transactional
	@TransactionalEventListener
	public void onItemSaleCounted(ItemSaleCountedEvent event) {
		event.getItemId().forEach(itemCommandRepository::incrementSales);
	}
}
