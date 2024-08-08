package com.team33.modulecore.cache;

import static java.util.stream.Collectors.*;

import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

import org.springframework.data.domain.Page;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
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

	private static final String MAIN_DISCOUNT_ITEM = "mainDiscountItem";
	private static final String MAIN_SALES_ITEM = "mainSalesItem";
	private static final int MAIN_ITEM_TIMEOUT = 7;
	private static final int CATEGORY_ITEM_TIMEOUT = 3;
	private static final String VIEW_COUNT = "view_count";

	private final RedisTemplate<String, Object> redisTemplate;
	private final ItemQueryRepository itemQueryRepository;

	public CachedMainItems getMainDiscountItem() {
		return getMainItemFromCache(MAIN_DISCOUNT_ITEM, itemQueryRepository::findItemsWithDiscountRateMain);
	}

	public CachedMainItems getMainSalesItem() {
		return getMainItemFromCache(MAIN_SALES_ITEM, itemQueryRepository::findItemsWithSalesMain);
	}

	public CachedCategoryItems<ItemQueryDto> getCategoryItems(
		CategoryName categoryName,
		String keyword,
		PriceFilter priceFilter,
		ItemPage pageDto
	) {
		ValueOperations<String, Object> ops = redisTemplate.opsForValue();

		CachedCategoryItems<ItemQueryDto> cachedCategoryItems =
			(CachedCategoryItems<ItemQueryDto>)ops.get(categoryName.name());

		if (cachedCategoryItems == null) {
			Page<ItemQueryDto> itemsByCategory =
				itemQueryRepository.findItemsByCategory(categoryName, keyword, priceFilter, pageDto);

			CachedCategoryItems<ItemQueryDto> value = new CachedCategoryItems<>(itemsByCategory);
			ops.set(categoryName.name(), value, CATEGORY_ITEM_TIMEOUT, TimeUnit.DAYS);

			return value;
		}

		return cachedCategoryItems;
	}

	public Map<String, Long> getViewCount() {

		HashOperations<String, String, Long> hash = redisTemplate.opsForHash();

		Map<String, Long> viewCount = extractViewCount(hash);

		resetViewCount(hash);

		return viewCount;
	}

	private CachedMainItems getMainItemFromCache(String key, Supplier<List<ItemQueryDto>> supplier) {
		ValueOperations<String, Object> ops = redisTemplate.opsForValue();
		CachedMainItems cachedMainItems = (CachedMainItems)ops.get(key);

		if (cachedMainItems == null) {
			List<ItemQueryDto> mainItem = supplier.get();
			CachedMainItems value = CachedMainItems.of(mainItem);

			ops.set(key, value, MAIN_ITEM_TIMEOUT, TimeUnit.DAYS);
			return value;
		}

		return cachedMainItems;
	}

	private Map<String, Long> extractViewCount(HashOperations<String, String, Long> hash) {
		return hash.entries(VIEW_COUNT).entrySet().stream()
			.filter(e -> e.getValue() > 0)
			.collect(toUnmodifiableMap(Map.Entry::getKey, Map.Entry::getValue));
	}

	private void resetViewCount(HashOperations<String, String, Long> hash) {
		hash.entries(VIEW_COUNT).entrySet().stream()
			.filter(e -> e.getValue() > 0)
			.forEach(e -> hash.put(VIEW_COUNT, e.getKey(), 0L));
	}
}
