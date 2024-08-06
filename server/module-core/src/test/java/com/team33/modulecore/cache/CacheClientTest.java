package com.team33.modulecore.cache;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.List;
import java.util.concurrent.TimeUnit;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.team33.modulecore.config.RedisTestConfig;
import com.team33.modulecore.core.item.domain.repository.ItemQueryRepository;
import com.team33.modulecore.core.item.dto.query.ItemQueryDto;

@JsonInclude(JsonInclude.Include.NON_NULL)
@ActiveProfiles("test")
@EnableAutoConfiguration
@ContextConfiguration(classes = RedisTestConfig.class)
@SpringBootTest
class CacheClientTest {

	@Autowired
	private RedisTemplate<String, Object> redisTemplate;
	@Autowired
	private RestTemplateBuilder restTemplateBuilder;

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
}