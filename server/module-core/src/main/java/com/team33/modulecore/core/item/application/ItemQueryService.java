package com.team33.modulecore.core.item.application;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import com.team33.modulecore.cache.CacheStrategyFactory;
import com.team33.modulecore.cache.ItemCacheManager;
import com.team33.modulecore.cache.dto.CachedCategoryItems;
import com.team33.modulecore.cache.dto.CachedItems;
import com.team33.modulecore.core.category.domain.CategoryName;
import com.team33.modulecore.core.item.domain.entity.Item;
import com.team33.modulecore.core.item.domain.repository.ItemQueryRepository;
import com.team33.modulecore.core.item.dto.query.ItemPage;
import com.team33.modulecore.core.item.dto.query.ItemQueryDto;
import com.team33.modulecore.core.item.dto.query.PriceFilter;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class ItemQueryService {

	private final ItemCacheManager itemCacheManager;
	private final ItemQueryRepository itemQueryRepository;
	private final CacheStrategyFactory cacheStrategyFactory;

	public Item findItemById(long itemId) {

		return itemQueryRepository.findById(itemId);
	}

	public List<ItemQueryDto> findMainDiscountItems() {

		CachedItems<ItemQueryDto> cachedItems = itemCacheManager.getMainDiscountItem();
		return cachedItems.getCachedItems();
	}

	public List<ItemQueryDto> findMainSaleItems() {

		CachedItems<ItemQueryDto> cachedItems = itemCacheManager.getMainSalesItem();
		return cachedItems.getCachedItems();
	}

	public Page<ItemQueryDto> findFilteredItem(
		String keyword,
		PriceFilter priceFilter,
		ItemPage pageDto
	) {

		return itemQueryRepository.findFilteredItems(keyword, priceFilter, pageDto);
	}

	public Page<ItemQueryDto> findItemOnSale(
		String keyword,
		ItemPage pageDto,
		PriceFilter priceFilter
	) {

		return itemQueryRepository.findItemsOnSale(keyword, priceFilter, pageDto);
	}

	public Page<ItemQueryDto> findByCategory(
		CategoryName categoryName,
		String keyword,
		PriceFilter priceFilter,
		ItemPage itemPage
	) {

		if (!canUseCache(keyword, priceFilter)) {
			return getItemsFromDB(categoryName, keyword, priceFilter, itemPage);
		}

		return cacheStrategyFactory.getCacheStrategy(itemPage.getSortOption())
			.map(strategy -> strategy.getCachedItems(categoryName, itemPage))
			.orElseGet(() -> getItemsFromDB(categoryName, keyword, priceFilter, itemPage));

	}

	public Page<ItemQueryDto> findByBrand(String brand, PriceFilter priceFilter, ItemPage itemPage) {

		return itemQueryRepository.findByBrand(brand, itemPage, priceFilter);
	}

	private boolean canUseCache(String keyword, PriceFilter priceFilter) {

		return priceFilter.isSumZero() && keyword.isEmpty();
	}

	private Page<ItemQueryDto> getItemsFromDB(CategoryName categoryName, String keyword, PriceFilter priceFilter,
		ItemPage itemPage) {

		Page<ItemQueryDto> itemsByCategoryPage = itemQueryRepository.findItemsByCategory(
			categoryName,
			keyword,
			priceFilter,
			itemPage
		);

		return new CachedCategoryItems<>(itemsByCategoryPage).toPage();
	}
}
