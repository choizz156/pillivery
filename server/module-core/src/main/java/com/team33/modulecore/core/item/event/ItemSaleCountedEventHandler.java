package com.team33.modulecore.core.item.event;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.event.TransactionalEventListener;

import com.team33.modulecore.core.item.domain.repository.ItemCommandRepository;
import com.team33.modulecore.exception.DataSaveException;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class ItemSaleCountedEventHandler {

	private final static Logger LOGGER = LoggerFactory.getLogger("fileLog");

	private final ItemCommandRepository itemCommandRepository;

	@Async
	@Transactional
	@TransactionalEventListener
	public void onItemSaleCounted(ItemSaleCountedEvent event) {
		increaseItemSale(event);
	}

	private void increaseItemSale(ItemSaleCountedEvent event) {
		try {
			event.getItemId().forEach(itemCommandRepository::incrementSales);
		} catch (Exception e) {
			LOGGER.warn("아이템 판매량 증가 중 오류가 발생 message = {} :: itemId = {}", e.getMessage(), event.getItemId());
			throw new DataSaveException(e.getMessage());
		}
	}
}
