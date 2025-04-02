package com.team33.modulecore.core.item.application;

import static org.mockito.Mockito.*;

import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.team33.modulecore.core.item.domain.repository.ItemCommandRepository;
import com.team33.modulecore.core.item.event.ItemSaleCountedEvent;
import com.team33.modulecore.core.item.event.ItemSaleCountedEventHandler;

class ItemSaleCountedEventHandlerTest {

	@DisplayName("아이템의 판매량을 늘릴 수 있다.")
	@Test
	void onItemSaleCounted() {

		ItemCommandRepository itemCommandRepository = mock(ItemCommandRepository.class);

		ItemSaleCountedEvent itemSaleCountedEvent = new ItemSaleCountedEvent(List.of(1L, 2L));
		ItemSaleCountedEventHandler itemSaleCountedEventHandler = new ItemSaleCountedEventHandler(itemCommandRepository);

		itemSaleCountedEventHandler.onItemSaleCounted(itemSaleCountedEvent);

		verify(itemCommandRepository, times(2)).incrementSales(anyLong());
	}
}