package com.team33.modulecore.cache;

import static com.team33.modulecore.cache.RedisCacheKey.*;
import static com.team33.modulecore.core.category.domain.CategoryName.*;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.redisson.api.RHyperLogLog;
import org.redisson.api.RMapCache;
import org.redisson.api.RSet;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.team33.modulecore.FixtureMonkeyFactory;
import com.team33.modulecore.cache.dto.CachedCategoryItems;
import com.team33.modulecore.cache.dto.CachedMainItems;
import com.team33.modulecore.core.category.domain.Categories;
import com.team33.modulecore.core.category.domain.CategoryName;
import com.team33.modulecore.core.item.domain.repository.ItemQueryRepository;
import com.team33.modulecore.core.item.dto.query.ItemPage;
import com.team33.modulecore.core.item.dto.query.ItemQueryDto;
import com.team33.modulecore.core.item.dto.query.PriceFilter;
import com.team33.moduleredis.config.EmbededRedisConfig;

@JsonInclude(JsonInclude.Include.NON_NULL)
@ActiveProfiles("test")
@EnableAutoConfiguration
@ContextConfiguration(classes = EmbededRedisConfig.class)
@SpringBootTest
class CacheClientTest {

	// @Autowired
	// private RedisTemplate<String, Object> redisTemplate;

	@Autowired
	private RedissonClient redissonClient;

	@DisplayName("메인 상품이 캐시되지 않았을 경우 캐싱을 한다.")
	@Test
	void 할인_캐싱_x() throws Exception {

		//given
		ItemQueryRepository itemQueryRepository = mock(ItemQueryRepository.class);
		when(itemQueryRepository.findItemsWithDiscountRateMain()).thenReturn(
			List.of(ItemQueryDto.builder()
				.enterprise("test")
				.build())
		);

		CacheClient cacheClient = new CacheClient(redissonClient, itemQueryRepository);

		//when
		CachedMainItems result = cacheClient.getMainDiscountItem();

		//then
		List<ItemQueryDto> mainItems = result.getCachedItems();

		RMapCache<String, CachedMainItems> cachedMainItems = redissonClient.getMapCache(CACHE_MAIN_ITEMS.name());
		CachedMainItems cachedMainItem = cachedMainItems.get(MAIN_DISCOUNT_ITEM.name());

		long expireTime = cachedMainItems.remainTimeToLive(MAIN_DISCOUNT_ITEM.name());
		long remainDay = TimeUnit.MILLISECONDS.toDays(expireTime);

		assertThat(remainDay).isEqualTo(6L);
		assertThat(mainItems).hasSize(1)
			.extracting("enterprise")
			.contains("test");

		assertThat(mainItems).usingRecursiveComparison().isEqualTo(cachedMainItem.getCachedItems());
	}

	@DisplayName("캐싱돼 있는 아이템이 있는 경우 db를 거치지 않는다.")
	@Test
	void 할인_캐싱_o() throws Exception {
		//given
		ItemQueryRepository itemQueryRepository = mock(ItemQueryRepository.class);
		when(itemQueryRepository.findItemsWithDiscountRateMain()).thenReturn(
			List.of(ItemQueryDto.builder()
				.enterprise("test")
				.build())
		);

		RMapCache<String, CachedMainItems> cachedMainItems = redissonClient.getMapCache(CACHE_MAIN_ITEMS.name());

		cachedMainItems.put(MAIN_DISCOUNT_ITEM.name(),
			CachedMainItems.of(
				List.of(ItemQueryDto.builder()
					.enterprise("test")
					.build()
				)
			), 7, TimeUnit.DAYS);

		CacheClient cacheClient = new CacheClient(redissonClient, itemQueryRepository);

		//when
		CachedMainItems mainItems = cacheClient.getMainDiscountItem();

		//then
		assertThat(mainItems.getCachedItems()).hasSize(1)
			.extracting("enterprise")
			.contains("test");

		verify(itemQueryRepository, times(0)).findItemsWithDiscountRateMain();
	}

	@DisplayName("카테고리별 아이템들이 캐싱되지 않았을 경우 캐싱한다. 8개")
	@Test
	void 카테고리_캐싱_x() throws Exception {
		//given
		List<ItemQueryDto> itemQueryDtos = FixtureMonkeyFactory.get().giveMeBuilder(ItemQueryDto.class)
			.set("categories", new Categories(Set.of(EYE)))
			.sampleList(8);

		Page<ItemQueryDto> page = PageableExecutionUtils.getPage(
			itemQueryDtos,
			PageRequest.of(0, 8),
			() -> 100
		);

		//given
		ItemQueryRepository itemQueryRepository = mock(ItemQueryRepository.class);
		when(itemQueryRepository.findItemsByCategory(
			any(CategoryName.class),
			eq(""),
			any(PriceFilter.class),
			any(ItemPage.class))
		)
			.thenReturn(page);

		CacheClient cacheClient = new CacheClient(redissonClient, itemQueryRepository);

		//when
		CachedCategoryItems<ItemQueryDto> categoryItems =
			cacheClient.getCategoryItems(EYE, "", new PriceFilter(), new ItemPage());

		//then
		List<ItemQueryDto> content = categoryItems.getContent();
		RMapCache<String, CachedCategoryItems<ItemQueryDto>> cachedCategoryItems =
			redissonClient.getMapCache(CATEGORY_ITEM.name());

		long ttl = cachedCategoryItems.remainTimeToLive("EYE");
		long remainingDay = TimeUnit.MILLISECONDS.toDays(ttl);

		assertThat(remainingDay).isEqualTo(2L);
		assertThat(cachedCategoryItems.get("EYE")).isInstanceOf(CachedCategoryItems.class);
		assertThat(content).hasSize(8).doesNotContainNull();
	}

	@DisplayName("카테고리별 아이템들이 캐싱되어 있을 경우 db를 거치지 않는다.")
	@Test
	void 카테고리_캐싱_o() throws Exception {
		//given
		List<ItemQueryDto> itemQueryDtos = FixtureMonkeyFactory.get().giveMeBuilder(ItemQueryDto.class)
			.set("categories", new Categories(Set.of(EYE)))
			.sampleList(8);

		Page<ItemQueryDto> page = PageableExecutionUtils.getPage(
			itemQueryDtos,
			PageRequest.of(0, 8),
			() -> 100
		);

		ItemQueryRepository itemQueryRepository = mock(ItemQueryRepository.class);
		when(itemQueryRepository.findItemsByCategory(any(CategoryName.class), eq(""), any(PriceFilter.class),
			any(ItemPage.class)))
			.thenReturn(page);

		RMapCache<String, CachedCategoryItems<ItemQueryDto>> cachedCategoryItems = redissonClient.getMapCache(
			CATEGORY_ITEM.name());
		cachedCategoryItems.put("EYE", new CachedCategoryItems<>(page), 3, TimeUnit.DAYS);

		CacheClient cacheClient = new CacheClient(redissonClient, itemQueryRepository);

		//when
		CachedCategoryItems<ItemQueryDto> categoryItems =
			cacheClient.getCategoryItems(EYE, "", new PriceFilter(), new ItemPage());

		//then
		List<ItemQueryDto> content = categoryItems.getContent();

		long ttl = cachedCategoryItems.remainTimeToLive("EYE");
		long remainingDay = TimeUnit.MILLISECONDS.toDays(ttl);

		assertThat(remainingDay).isEqualTo(2L);
		assertThat(cachedCategoryItems.get("EYE")).isInstanceOf(CachedCategoryItems.class);
		assertThat(content).hasSize(8).doesNotContainNull();

		verify(itemQueryRepository, times(0)).findItemsByCategory(any(CategoryName.class), eq(""),
			any(PriceFilter.class), any(ItemPage.class));
	}

	@DisplayName("특정 아이템들의 조회수를 반환할 수 있다.")
	@Test
	void 아이템_조회수() throws Exception {
		//given
		RSet<Integer> viewedItem = redissonClient.getSet(VIEW_COUNT.name());
		viewedItem.add(11);
		viewedItem.add(22);
		viewedItem.add(33);

		RHyperLogLog<Long> item1Count = redissonClient.getHyperLogLog(String.valueOf(11));
		RHyperLogLog<Long> item2Count = redissonClient.getHyperLogLog(String.valueOf(22));
		RHyperLogLog<Long> item3Count = redissonClient.getHyperLogLog(String.valueOf(33));

		for (long i = 0; i < 100; i++) {
			item1Count.add(i);
		}

		for (long i = 0; i < 50; i++) {
			item2Count.add(i);
		}

		for (long i = 0; i < 30; i++) {
			item3Count.add(i);
		}

		CacheClient cacheClient = new CacheClient(redissonClient, null);

		//when
		Map<String, Long> viewCount = cacheClient.getViewCount();

		//then
		assertThat(viewCount.get("11")).isEqualTo(100L);
		assertThat(viewCount.get("22")).isEqualTo(50L);
		assertThat(viewCount.get("33")).isEqualTo(30L);

		assertThat(item1Count.count()).isZero();
		assertThat(item2Count.count()).isZero();
		assertThat(item3Count.count()).isZero();
	}
}