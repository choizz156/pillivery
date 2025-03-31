package com.team33.modulecore.cache;

import static com.team33.modulecore.cache.RedisCacheKey.*;
import static java.util.stream.Collectors.*;

import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

import org.redisson.api.RMapCache;
import org.redisson.api.RSet;
import org.redisson.api.RedissonClient;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

import com.team33.modulecore.cache.dto.CachedCategoryItems;
import com.team33.modulecore.cache.dto.CachedMainItems;
import com.team33.modulecore.core.category.domain.CategoryName;
import com.team33.modulecore.core.item.domain.repository.ItemQueryRepository;
import com.team33.modulecore.core.item.dto.query.ItemPage;
import com.team33.modulecore.core.item.dto.query.ItemQueryDto;
import com.team33.modulecore.core.item.dto.query.PriceFilter;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Component
public class CacheClient {

	private static final int CATEGORY_ITEM_TIMEOUT = 3;
	private static final int MAIN_ITEM_TIMEOUT = 7;
	private final RedissonClient redissonClient;

	private final ItemQueryRepository itemQueryRepository;

	public CachedMainItems getMainDiscountItem() {
		return getMainItemFromCache(MAIN_DISCOUNT_ITEM.name(), itemQueryRepository::findItemsWithDiscountRateMain);
	}

	public CachedMainItems getMainSalesItem() {
		return getMainItemFromCache(MAIN_SALES_ITEM.name(), itemQueryRepository::findItemsWithSalesMain);
	}

	public CachedCategoryItems<ItemQueryDto> getCategoryItems(
		CategoryName categoryName,
		String keyword,
		PriceFilter priceFilter,
		ItemPage pageDto
	) {
		RMapCache<String, CachedCategoryItems<ItemQueryDto>> cachedCategoryItems =
			redissonClient.getMapCache(CATEGORY_ITEM.name());

		CachedCategoryItems<ItemQueryDto> categoryItems = cachedCategoryItems.get(categoryName.name());

		if (categoryItems == null) {
			Page<ItemQueryDto> itemsByCategory =
				itemQueryRepository.findItemsByCategory(categoryName, keyword, priceFilter, pageDto);

			CachedCategoryItems<ItemQueryDto> value = new CachedCategoryItems<>(itemsByCategory);
			cachedCategoryItems.put(categoryName.name(), value, CATEGORY_ITEM_TIMEOUT, TimeUnit.DAYS);

			return value;
		}

		return categoryItems;
	}

	public Map<String, Long> getViewCount() {

		RSet<Integer> viewedItems = redissonClient.getSet(VIEW_COUNT.name());

		Map<String, Long> viewCountOfEachItem = extractViewCount(viewedItems);
		resetViewCount(viewedItems);

		return viewCountOfEachItem;
	}

	private CachedMainItems getMainItemFromCache(String key, Supplier<List<ItemQueryDto>> supplier) {
		RMapCache<String, CachedMainItems> cachedMainItems = redissonClient.getMapCache(CACHE_MAIN_ITEMS.name());
		CachedMainItems mainItems = cachedMainItems.get(key);

		if (mainItems == null) {
			List<ItemQueryDto> mainItem = supplier.get();
			CachedMainItems value = CachedMainItems.of(mainItem);

			cachedMainItems.put(key, value, MAIN_ITEM_TIMEOUT, TimeUnit.DAYS);
			return value;
		}

		return mainItems;
	}

	private Map<String, Long> extractViewCount(RSet<Integer> viewCount) {
		return viewCount.stream()
			.collect(toUnmodifiableMap(String::valueOf, e -> redissonClient.getHyperLogLog(String.valueOf(e)).count()));
	}

	private void resetViewCount(RSet<Integer> viewCount) {
		viewCount
			.forEach(e -> redissonClient.getHyperLogLog(String.valueOf(e)).delete());
	}
}
