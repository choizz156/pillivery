package com.team33.modulecore.core.item.event;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.transaction.Transactional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.event.TransactionalEventListener;

import com.team33.modulecore.core.item.domain.repository.ItemSalesBatchDao;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@EnableScheduling
@Service
public class ItemSaleCountedEventHandler {

	private final static Logger LOGGER = LoggerFactory.getLogger("fileLog");

	private final ItemSalesBatchDao itemSalesBatchDao;

	private final ConcurrentHashMap<Long, Long> saleCounts = new ConcurrentHashMap<>();

	@Async
	@TransactionalEventListener
	public void onItemSaleCounted(ItemSaleCountedEvent event) {

		event.getItemId().forEach(itemId -> {
			saleCounts.merge(itemId, 1L, Long::sum);
		});
	}

	@Scheduled(fixedDelay = 60000L)
	@Transactional
	public void increaseItemSale() {

		Map<Long, Long> batch = new HashMap<>(saleCounts);

		try {
			itemSalesBatchDao.updateAll(batch);
			LOGGER.info("batch = {} :: increaseItemSale", batch.keySet());
			saleCounts.keySet().removeAll(batch.keySet());
		} catch (Exception e) {
			LOGGER.warn("아이템 판매량 증가 중 오류가 발생 message = {}", e.getMessage());
		}
	}
}
