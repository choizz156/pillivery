package com.team33.modulecore.cache;

import static com.team33.modulecore.core.category.domain.CategoryName.*;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.team33.modulecore.FixtureMonkeyFactory;
import com.team33.modulecore.cache.dto.CachedCategoryItems;
import com.team33.modulecore.cache.dto.CachedMainItems;
import com.team33.modulecore.config.RedisTestConfig;
import com.team33.modulecore.core.category.domain.Categories;
import com.team33.modulecore.core.category.domain.CategoryName;
import com.team33.modulecore.core.item.domain.repository.ItemQueryRepository;
import com.team33.modulecore.core.item.dto.query.ItemPage;
import com.team33.modulecore.core.item.dto.query.ItemQueryDto;
import com.team33.modulecore.core.item.dto.query.PriceFilter;

@JsonInclude(JsonInclude.Include.NON_NULL)
@ActiveProfiles("test")
@EnableAutoConfiguration
@ContextConfiguration(classes = RedisTestConfig.class)
@SpringBootTest
class CacheClientTest {

	@Autowired
	private RedisTemplate<String, Object> redisTemplate;

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

		CacheClient cacheClient = new CacheClient(redisTemplate, itemQueryRepository);

		//when
		CachedMainItems result = cacheClient.getMainDiscountItem();

		//then
		List<ItemQueryDto> mainItems = result.getCachedItems();

		ValueOperations<String, Object> ops = redisTemplate.opsForValue();
		CachedMainItems cachedMainItems = (CachedMainItems)ops.get("mainDiscountItem");

		Long expireTime = redisTemplate.getExpire("mainDiscountItem", TimeUnit.DAYS); //남은 만료시간

		assertThat(expireTime).isEqualTo(6L);
		assertThat(mainItems).hasSize(1)
			.extracting("enterprise")
			.contains("test");

		assertThat(mainItems).usingRecursiveComparison().isEqualTo(cachedMainItems.getCachedItems());
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

		ValueOperations<String, Object> ops = redisTemplate.opsForValue();
		ops.set("mainDiscountItem",
			CachedMainItems.of(
				List.of(ItemQueryDto.builder()
					.enterprise("test")
					.build()
				)
			), 7, TimeUnit.DAYS);

		CacheClient cacheClient = new CacheClient(redisTemplate, itemQueryRepository);

		//when
		CachedMainItems cachedMainItems = cacheClient.getMainDiscountItem();

		//then
		assertThat(cachedMainItems.getCachedItems()).hasSize(1)
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

		CacheClient cacheClient = new CacheClient(redisTemplate, itemQueryRepository);

		//when
		CachedCategoryItems<ItemQueryDto> categoryItems =
			cacheClient.getCategoryItems(EYE, "", new PriceFilter(), new ItemPage());

		//then
		List<ItemQueryDto> content = categoryItems.getContent();
		ValueOperations<String, Object> ops = redisTemplate.opsForValue();
		Long expireTime = redisTemplate.getExpire("EYE", TimeUnit.DAYS); //남은 만료시간

		assertThat(expireTime).isEqualTo(2L);
		assertThat(ops.get("EYE")).isInstanceOf(CachedCategoryItems.class);
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

		ValueOperations<String, Object> ops = redisTemplate.opsForValue();
		ops.set("EYE", new CachedCategoryItems<>(page), 3, TimeUnit.DAYS);

		CacheClient cacheClient = new CacheClient(redisTemplate, itemQueryRepository);

		//when
		CachedCategoryItems<ItemQueryDto> categoryItems =
			cacheClient.getCategoryItems(EYE, "", new PriceFilter(), new ItemPage());

		//then
		List<ItemQueryDto> content = categoryItems.getContent();
		Long expireTime = redisTemplate.getExpire("EYE", TimeUnit.DAYS); //남은 만료시간

		assertThat(expireTime).isEqualTo(2L);
		assertThat(ops.get("EYE")).isInstanceOf(CachedCategoryItems.class);
		assertThat(content).hasSize(8).doesNotContainNull();

		verify(itemQueryRepository, times(0)).findItemsByCategory(any(CategoryName.class), eq(""),
			any(PriceFilter.class), any(ItemPage.class));
	}
}