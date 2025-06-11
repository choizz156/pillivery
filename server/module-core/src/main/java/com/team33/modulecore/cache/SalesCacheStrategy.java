package com.team33.modulecore.cache;

import org.springframework.data.domain.Page;

import com.team33.modulecore.core.category.domain.CategoryName;
import com.team33.modulecore.core.item.domain.ItemSortOption;
import com.team33.modulecore.core.item.dto.query.ItemPage;
import com.team33.modulecore.core.item.dto.query.ItemQueryDto;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class SalesCacheStrategy implements CacheStrategy<ItemQueryDto> {

	private final ItemCacheManager itemCacheManager;

	@Override
	public Page<ItemQueryDto> getCachedItems(CategoryName categoryName, ItemPage itemPage) {
		return itemCacheManager.getCategoryItemsOnSales(categoryName, itemPage).toPage();
	}

	@Override
	public boolean supports(ItemSortOption itemSortOption) {
		return itemSortOption == ItemSortOption.SALES;
	}
}
