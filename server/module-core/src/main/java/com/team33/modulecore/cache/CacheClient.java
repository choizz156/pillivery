package com.team33.modulecore.cache;

import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Component;

import com.team33.modulecore.core.item.domain.repository.ItemQueryRepository;
import com.team33.modulecore.core.item.dto.query.ItemQueryDto;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Component
public class CacheClient {

	private static final String MAIN_DISCOUNT_ITEM = "mainDiscountItem";
	private static final String MAIN_SALES_ITEM = "mainSalesItem";
	private static final int TIMEOUT = 7;

	private final RedisTemplate<String, Object> redisTemplate;
	private final ItemQueryRepository itemQueryRepository;

	public CachedItems getMainDiscountItem() {
		return getMainItemFromCache(MAIN_DISCOUNT_ITEM, itemQueryRepository::findItemsWithDiscountRateMain);
	}

	public CachedItems getMainSalesItem() {
		return getMainItemFromCache(MAIN_SALES_ITEM, itemQueryRepository::findItemsWithSalesMain);
	}

	private CachedItems getMainItemFromCache(String key, Supplier<List<ItemQueryDto>> supplier) {
		ValueOperations<String, Object> ops = redisTemplate.opsForValue();
		CachedItems cachedItems = (CachedItems)ops.get(key);

		if (cachedItems == null) {
			List<ItemQueryDto> mainItem = supplier.get();
			CachedItems value = CachedItems.of(mainItem);

			ops.set(key, value, TIMEOUT, TimeUnit.DAYS);
			return value;
		}

		return cachedItems;
	}
}
