package com.team33.modulecore.cache;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.redisson.spring.starter.RedissonAutoConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.data.domain.PageImpl;
import org.springframework.test.context.ActiveProfiles;

import com.team33.modulecore.cache.dto.CachedCategoryItems;
import com.team33.modulecore.cache.dto.CachedItems;
import com.team33.modulecore.config.CacheConfig;
import com.team33.modulecore.core.category.domain.CategoryName;
import com.team33.modulecore.core.item.domain.repository.ItemQueryRepository;
import com.team33.modulecore.core.item.dto.query.ItemPage;
import com.team33.modulecore.core.item.dto.query.ItemQueryDto;
import com.team33.modulecore.core.item.dto.query.PriceFilter;

@ActiveProfiles("test")
@EnableAutoConfiguration(exclude = {RedissonAutoConfiguration.class})
@EnableCaching
@SpringBootTest(
	webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
	classes = {CacheConfig.class, CacheClient.class}
)
class CacheClientTest {

	@MockBean
	private ItemQueryRepository itemQueryRepository;
	@Autowired
	private CacheManager cacheManager;
	@Autowired
	private CacheClient cacheClient;

	private List<ItemQueryDto> mockItems = List.of(
		ItemQueryDto.builder().itemId(1L).productName("item2").discountPrice(2000).discountRate(0).build()
	);

	@BeforeEach
	void setUp() {

		cacheManager.getCache(CacheType.MAIN_ITEMS.name()).clear();
		cacheManager.getCache(CacheType.CATEGORY_ITEMS.name()).clear();
	}

	@DisplayName("할인 상품 캐시가 없을 때 DB에서 조회한다")
	@Test
	void test1() throws Exception {
		// given
		when(itemQueryRepository.findItemsWithDiscountRateMain()).thenReturn(mockItems);

		// when
		CachedItems<ItemQueryDto> result = cacheClient.getMainDiscountItem();

		// then
		assertThat(result.getCachedItems()).hasSize(1);
		verify(itemQueryRepository, times(1)).findItemsWithDiscountRateMain();
	}

	@DisplayName("할인 상품 캐시가 있을 때 DB 조회하지 않는다")
	@Test
	void test2() throws Exception {
		// given
		when(itemQueryRepository.findItemsWithDiscountRateMain()).thenReturn(mockItems);

		//미리 캐싱
		cacheClient.getMainDiscountItem();

		// when
		CachedItems result = cacheClient.getMainDiscountItem();

		// then
		Cache.ValueWrapper discount = cacheManager.getCache(CacheType.MAIN_ITEMS.name()).get("discount");
		assertThat(discount.get()).isInstanceOf(CachedItems.class);
		assertThat(result.getCachedItems()).hasSize(1);
		//캐싱이 안됐으면 2번 호출해야함.
		verify(itemQueryRepository, times(1)).findItemsWithDiscountRateMain();
	}

	@DisplayName("판매량 기준 상품 캐시가 없을 때 DB에서 조회한다")
	@Test
	void test3() throws Exception {
		// given
		when(itemQueryRepository.findItemsWithSalesMain()).thenReturn(mockItems);

		// when
		CachedItems result = cacheClient.getMainSalesItem();

		// then
		assertThat(result.getCachedItems()).hasSize(1);
		verify(itemQueryRepository, times(1)).findItemsWithSalesMain();
	}

	@DisplayName("판매량 기준 상품 캐시가 있을 때 DB 조회하지 않는다")
	@Test
	void test4() {
		// given
		when(itemQueryRepository.findItemsWithSalesMain()).thenReturn(mockItems);

		cacheClient.getMainSalesItem();

		// when
		CachedItems result = cacheClient.getMainSalesItem();

		// then
		Cache.ValueWrapper sales = cacheManager.getCache(CacheType.MAIN_ITEMS.name()).get("sales");
		assertThat(sales.get()).isInstanceOf(CachedItems.class);
		assertThat(result.getCachedItems()).hasSize(1);
		verify(itemQueryRepository, times(1)).findItemsWithSalesMain();
	}

	@DisplayName("카테고리별 상품 캐시가 없을 때 DB에서 조회한다")
	@Test
	void getCategoryItems_NoCache() {
		// given
		CategoryName categoryName = CategoryName.BONE;
		String keyword = "골격근";
		PriceFilter priceFilter = new PriceFilter(1000, 5000);
		ItemPage pageDto = ItemPage.builder().page(1).size(8).build();

		PageImpl<ItemQueryDto> mockPage = new PageImpl<>(mockItems);

		when(itemQueryRepository.findItemsByCategory(
			categoryName, keyword, priceFilter, pageDto
		)).thenReturn(mockPage);

		// when
		CachedCategoryItems<ItemQueryDto> result = cacheClient.getCategoryItems(
			categoryName, keyword, priceFilter, pageDto
		);

		// then
		assertThat(result.getContent().getCachedItems()).hasSize(1);
		verify(itemQueryRepository, times(1)).findItemsByCategory(
			categoryName, keyword, priceFilter, pageDto
		);
	}

	@DisplayName("카테고리별 상품 캐시가 있을 때 DB 조회하지 않는다")
	@Test
	void getCategoryItems_WithCache() {
		// given
		CategoryName categoryName = CategoryName.BONE;
		String keyword = "골격";
		PriceFilter priceFilter = new PriceFilter(1000, 5000);
		ItemPage pageDto = new ItemPage();

		PageImpl<ItemQueryDto> mockPage = new PageImpl<>(mockItems);

		when(itemQueryRepository.findItemsByCategory(
			categoryName, keyword, priceFilter, pageDto
		)).thenReturn(mockPage);

		cacheClient.getCategoryItems(categoryName, keyword, priceFilter, pageDto);

		// when
		CachedCategoryItems<ItemQueryDto> result = cacheClient.getCategoryItems(
			categoryName, keyword, priceFilter, pageDto
		);

		// then
		assertThat(result.getContent().getCachedItems()).hasSize(1);
		verify(itemQueryRepository, times(1)).findItemsByCategory(
			categoryName, keyword, priceFilter, pageDto
		);
	}
}

