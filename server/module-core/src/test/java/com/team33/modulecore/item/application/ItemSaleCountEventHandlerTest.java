package com.team33.modulecore.item.application;

import static org.mockito.Mockito.*;

import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.context.ApplicationContext;

import com.team33.modulecore.item.domain.repository.ItemCommandRepository;
import com.team33.modulecore.order.events.ItemSaleCountedEvent;

class ItemSaleCountEventHandlerTest {

	@DisplayName("아이템의 판매량을 늘릴 수 있다.")
	@Test
	void onItemSaleCounted() {

		ItemCommandRepository itemCommandRepository = mock(ItemCommandRepository.class);

		ItemSaleCountedEvent itemSaleCountedEvent = new ItemSaleCountedEvent(List.of(1L, 2L));
		ItemSaleCountEventHandler itemSaleCountEventHandler = new ItemSaleCountEventHandler(itemCommandRepository);

		itemSaleCountEventHandler.onItemSaleCounted(itemSaleCountedEvent);

		verify(itemCommandRepository, times(2)).incrementSales(anyLong());
	}


	@DisplayName("아이템 판매량 카운트 이벤트를 처리할 수 있다.")
	@Test
	void event() throws Exception {

		//given
		ArgumentCaptor<ItemSaleCountedEvent> eventArgumentCaptor = ArgumentCaptor.forClass(ItemSaleCountedEvent.class);

		ApplicationContext applicationContext = mock(ApplicationContext.class);
		ItemSaleCountEventHandler itemSaleCountEventHandler = mock(ItemSaleCountEventHandler.class);
		ItemSaleCountedEvent itemSaleCountedEvent = new ItemSaleCountedEvent(List.of(1L, 2L));

		applicationContext.publishEvent(itemSaleCountedEvent);

		verify(applicationContext).publishEvent(eventArgumentCaptor.capture());
		ItemSaleCountedEvent captorValue = eventArgumentCaptor.getValue();

		//when
		itemSaleCountEventHandler.onItemSaleCounted(captorValue);

		//then
		verify(itemSaleCountEventHandler, times(1)).onItemSaleCounted(any(ItemSaleCountedEvent.class));
	}
}