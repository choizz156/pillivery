package com.team33.modulecore.cache;

import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

import org.springframework.data.domain.Page;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Component;

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

	private final RedisTemplate<String, Object> redisTemplate;
	private final ItemQueryRepository itemQueryRepository;

	public CachedMainItems getMainDiscountItem() {
		return getMainItemFromCache(MAIN_DISCOUNT_ITEM, itemQueryRepository::findItemsWithDiscountRateMain);
	}

	public CachedMainItems getMainSalesItem() {
		return getMainItemFromCache(MAIN_SALES_ITEM, itemQueryRepository::findItemsWithSalesMain);
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

	public CachedCategoryItems<ItemQueryDto> getCategoryItems(CategoryName categoryName, String keyword, PriceFilter priceFilter, ItemPage pageDto) {
		ValueOperations<String, Object> ops = redisTemplate.opsForValue();
		CachedCategoryItems<ItemQueryDto> cachedCategoryItems = (CachedCategoryItems<ItemQueryDto>)ops.get(categoryName.name());

		if(cachedCategoryItems == null) {
			Page<ItemQueryDto> itemsByCategory =
				itemQueryRepository.findItemsByCategory(categoryName, keyword, priceFilter, pageDto);

			CachedCategoryItems<ItemQueryDto> value = new CachedCategoryItems<>(itemsByCategory);
			ops.set(categoryName.name(), value, CATEGORY_ITEM_TIMEOUT, TimeUnit.DAYS);

			return value;
		}

		return cachedCategoryItems;
	}
}
