package com.team33.modulecore.cache;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Component;

import com.team33.modulecore.core.item.domain.ItemSortOption;
import com.team33.modulecore.core.item.dto.query.ItemQueryDto;

@Component
public class CacheStrategyFactory {

	private final List<CacheStrategy<ItemQueryDto>> cacheStrategies;

	public CacheStrategyFactory(ItemCacheManager itemCacheManager) {
		this.cacheStrategies = List.of(
			new SalesCacheStrategy(itemCacheManager),
			new DiscountCacheStrategy(itemCacheManager)
		);
	}

	public Optional<CacheStrategy<ItemQueryDto>> getCacheStrategy(ItemSortOption sortOption) {
		return cacheStrategies.stream()
			.filter(strategy -> strategy.supports(sortOption))
			.findFirst();
	}

}
