package com.team33.modulecore.item.application;

import static org.mockito.Mockito.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.team33.modulecore.category.domain.CategoryName;
import com.team33.modulecore.item.domain.repository.ItemQueryRepository;
import com.team33.modulecore.item.dto.query.ItemPage;
import com.team33.modulecore.item.dto.query.PriceFilter;

class ItemQueryServiceTest {

	private ItemQueryRepository itemQueryRepository;
	private ItemQueryService itemQueryService;

	@BeforeEach
	void setUp() {
		itemQueryRepository = mock(ItemQueryRepository.class);
		itemQueryService = new ItemQueryService(itemQueryRepository);
	}

	@DisplayName("할인 중인 메인 아이템들을 조회할 수 있다.")
	@Test
	void 아이템_조회() throws Exception {
		//given
		when(itemQueryRepository.findItemsWithDiscountRateMain()).thenReturn(null);

		//when
		itemQueryService.findMainDiscountItems();

		//then
		verify(itemQueryRepository, times(1)).findItemsWithDiscountRateMain();
	}

	@DisplayName("판매량이 많은 메인 아이템들을 조회할 수 있다.")
	@Test
	void 아이템_조회2() throws Exception {
		//given
		when(itemQueryRepository.findItemsWithSalesMain()).thenReturn(null);

		//when
		itemQueryService.findMainSaleItems();

		//then
		verify(itemQueryRepository, times(1)).findItemsWithSalesMain();
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

	@DisplayName("카테고리에 맞는 아이템을 조회할 수 있다.")
	@Test
	void 아이템_조회5() throws Exception {
		//given
		when(itemQueryRepository.findByBrand(anyString(), any(ItemPage.class), any(PriceFilter.class))).thenReturn(
			null);

		//when
		itemQueryService.findByBrand("test", new ItemPage(), new PriceFilter());

		//then
		verify(itemQueryRepository, times(1)).findByBrand(anyString(), any(ItemPage.class), any(PriceFilter.class));
	}

	@DisplayName("브랜드에 맞는 아이템을 조죄할 수 있다.")
	@Test
	void 아이템_조회6() throws Exception {
		//given
		when(itemQueryRepository.findItemsByCategory(
			any(CategoryName.class), anyString(), any(PriceFilter.class), any(ItemPage.class))
		).thenReturn(null);

		//when
		itemQueryService.findByCategory(CategoryName.EYE, "test", new PriceFilter(), new ItemPage());

		//then
		verify(itemQueryRepository, times(1)).
			findItemsByCategory(
				any(CategoryName.class), anyString(),
				any(PriceFilter.class),
				any(ItemPage.class)
			);
	}
}