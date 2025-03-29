package com.team33.moduleevent.handler;

import static org.mockito.Mockito.*;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.team33.modulecore.core.item.application.ItemSaleCountEventHandler;
import com.team33.modulecore.core.item.domain.repository.ItemCommandRepository;
import com.team33.modulecore.core.order.events.ItemSaleCountedEvent;

class ItemSaleCountedEventHandlerTest {

	private ItemCommandRepository itemCommandRepository;
	private ItemSaleCountEventHandler itemSaleCountEventHandler;

	@BeforeEach
	void setUp() {

		itemCommandRepository = mock(ItemCommandRepository.class);
		itemSaleCountEventHandler = new ItemSaleCountEventHandler(itemCommandRepository);
	}

	@DisplayName("아이템 판매 수량 이벤트 발생 시 각 아이템의 판매 수량이 증가한다")
	@Test
	void test1() {
		// given
		List<Long> itemIds = List.of(1L, 2L, 3L);
		ItemSaleCountedEvent event = new ItemSaleCountedEvent(itemIds);

		// when
		itemSaleCountEventHandler.onItemSaleCounted(event);

		// then
		verify(itemCommandRepository).incrementSales(1L);
		verify(itemCommandRepository).incrementSales(2L);
		verify(itemCommandRepository).incrementSales(3L);
	}

	@DisplayName("아이템 ID가 없는 경우 아무 작업도 수행하지 않는다")
	@Test
	void test2() {
		// given
		List<Long> emptyItemIds = List.of();
		ItemSaleCountedEvent event = new ItemSaleCountedEvent(emptyItemIds);

		// when
		itemSaleCountEventHandler.onItemSaleCounted(event);

		// then
		verifyNoInteractions(itemCommandRepository);
	}

	@DisplayName("여러 개의 같은 아이템 ID가 포함된 경우 각 ID마다 한 번씩 증가한다")
	@Test
	void test3() {
		// given
		List<Long> duplicateItemIds = List.of(1L, 1L, 2L);
		ItemSaleCountedEvent event = new ItemSaleCountedEvent(duplicateItemIds);

		// when
		itemSaleCountEventHandler.onItemSaleCounted(event);

		// then
		verify(itemCommandRepository, times(2)).incrementSales(1L);
		verify(itemCommandRepository, times(1)).incrementSales(2L);
	}
}