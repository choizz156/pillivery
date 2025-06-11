package com.team33.modulecore.core.item.application;

import static org.mockito.Mockito.*;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.support.PageableExecutionUtils;

import com.team33.modulecore.cache.CacheStrategyFactory;
import com.team33.modulecore.cache.ItemCacheManager;
import com.team33.modulecore.cache.dto.CachedItems;
import com.team33.modulecore.core.category.domain.CategoryName;
import com.team33.modulecore.core.item.domain.repository.ItemQueryRepository;
import com.team33.modulecore.core.item.dto.query.ItemPage;
import com.team33.modulecore.core.item.dto.query.ItemQueryDto;
import com.team33.modulecore.core.item.dto.query.PriceFilter;

class ItemQueryServiceTest {

	private ItemQueryRepository itemQueryRepository;
	private ItemQueryService itemQueryService;
	private ItemCacheManager itemCacheManager;

	@BeforeEach
	void setUp() {

		itemQueryRepository = mock(ItemQueryRepository.class);
		itemCacheManager = mock(ItemCacheManager.class);
		itemQueryService = new ItemQueryService(itemCacheManager, itemQueryRepository, new CacheStrategyFactory(itemCacheManager));
	}

	@DisplayName("할인 중인 메인 아이템들을 조회할 수 있다.")
	@Test
	void 아이템_조회() throws Exception {
		//given
		when(itemCacheManager.getMainDiscountItem()).thenReturn(CachedItems.of(List.of()));

		//when
		itemQueryService.findMainDiscountItems();

		//then
		verify(itemCacheManager, times(1)).getMainDiscountItem();
	}

	@DisplayName("판매량이 많은 메인 아이템들을 조회할 수 있다.")
	@Test
	void 아이템_조회2() throws Exception {
		//given
		when(itemCacheManager.getMainSalesItem()).thenReturn(CachedItems.of(List.of()));

		//when
		itemQueryService.findMainSaleItems();

		//then
		verify(itemCacheManager, times(1)).getMainSalesItem();
	}

	@DisplayName("조회 필터링이 있는 아이템들을 조회할 수 있다.")
	@Test
	void 아이템_조회3() throws Exception {
		//given
		when(
			itemQueryRepository.findFilteredItems(anyString(), any(PriceFilter.class), any(ItemPage.class))
		)
			.thenReturn(null);

		//when
		itemQueryService.findFilteredItem("test", new PriceFilter(), new ItemPage());

		//then
		verify(itemQueryRepository, times(1)).findFilteredItems(anyString(), any(PriceFilter.class),
			any(ItemPage.class));
	}

	@DisplayName("할인 중인 아이템을 조회할 수 있다.")
	@Test
	void 아이템_조회4() throws Exception {
		//given
		when(itemQueryRepository.findItemsOnSale(anyString(), any(PriceFilter.class), any(ItemPage.class))).thenReturn(
			null);

		//when
		itemQueryService.findItemOnSale("test", new ItemPage(), new PriceFilter());

		//then
		verify(itemQueryRepository, times(1)).findItemsOnSale(anyString(), any(PriceFilter.class), any(ItemPage.class));
	}

	@DisplayName("브랜드에 맞는 아이템을 조회할 수 있다.")
	@Test
	void 아이템_조회5() throws Exception {
		//given
		when(itemQueryRepository.findByBrand(anyString(), any(ItemPage.class), any(PriceFilter.class)))
			.thenReturn(null);

		//when
		itemQueryService.findByBrand("test", new PriceFilter(), new ItemPage());

		//then
		verify(itemQueryRepository, times(1)).findByBrand(anyString(), any(ItemPage.class), any(PriceFilter.class));
	}

	@DisplayName("카테고리에 맞는 아이템을 조회할 수 있다.")
	@Test
	void 아이템_조회6() throws Exception {
		//given
		Page<ItemQueryDto> page = PageableExecutionUtils.getPage(
			List.of(ItemQueryDto.builder().build()),
			PageRequest.of(0, 8),
			() -> 100
		);

		when(itemQueryRepository.findItemsByCategory(any(), anyString(), any(), any())).thenReturn(page);

		//when
		itemQueryService.findByCategory(CategoryName.EYE, "test", new PriceFilter(), new ItemPage());

		//then
		verify(itemQueryRepository, times(1)).findItemsByCategory(
			any(), anyString(), any(), any()
		);
	}
}