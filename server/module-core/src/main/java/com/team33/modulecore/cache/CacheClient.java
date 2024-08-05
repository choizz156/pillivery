package com.team33.modulecore.cache;

import java.util.List;
import java.util.concurrent.TimeUnit;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;

import com.team33.modulecore.core.item.domain.repository.ItemQueryRepository;
import com.team33.modulecore.core.item.dto.query.ItemQueryDto;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class CacheClient {

	private static final String MAIN_DISCOUNT_ITEM = "mainDiscountItem";
	private static final String MAIN_SALES_ITEM = "mainSalesItem";

	private final RedisTemplate<String, Object> redisTemplate;
	private final ItemQueryRepository itemQueryRepository;

	public List<ItemQueryDto> getMainDiscountItem() {
		ValueOperations<String, Object> ops = redisTemplate.opsForValue();
		CachedItems cachedItems = (CachedItems)ops.get(MAIN_DISCOUNT_ITEM);

		if(cachedItems == null){
			List<ItemQueryDto> itemsWithDiscountRateMain = itemQueryRepository.findItemsWithDiscountRateMain();

			ops.set(MAIN_DISCOUNT_ITEM, CachedItems.of(itemsWithDiscountRateMain), 7, TimeUnit.DAYS);
			return itemsWithDiscountRateMain;
		}

		return cachedItems.getMainItems();
	}

	public List<ItemQueryDto> getMainSalesItem() {
		ValueOperations<String, Object> ops = redisTemplate.opsForValue();
		CachedItems cachedItems = (CachedItems)ops.get(MAIN_SALES_ITEM);

		if(cachedItems == null){
			List<ItemQueryDto> itemsWithSalesMain = itemQueryRepository.findItemsWithSalesMain();

			ops.set(MAIN_SALES_ITEM, itemsWithSalesMain, 7, TimeUnit.DAYS);
			return itemsWithSalesMain;
		}
		return cachedItems.getMainItems();
	}
}
