package com.team33.modulecore.core.item.application;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import com.team33.modulecore.cache.CacheClient;
import com.team33.modulecore.cache.CachedCategoryItems;
import com.team33.modulecore.cache.CachedMainItems;
import com.team33.modulecore.core.category.domain.CategoryName;
import com.team33.modulecore.core.item.domain.repository.ItemQueryRepository;
import com.team33.modulecore.core.item.dto.query.ItemPage;
import com.team33.modulecore.core.item.dto.query.ItemQueryDto;
import com.team33.modulecore.core.item.dto.query.PriceFilter;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class ItemQueryService {

	private static final Logger log = LoggerFactory.getLogger(ItemQueryService.class);
	private final CacheClient cacheClient;
	private final ItemQueryRepository itemQueryRepository;

	public List<ItemQueryDto> findMainDiscountItems() {
		// return itemQueryRepository.findItemsWithDiscountRateMain();
		CachedMainItems cachedMainItems = cacheClient.getMainDiscountItem();
		return cachedMainItems.getCachedItems();
	}

	public List<ItemQueryDto> findMainSaleItems() {
		// return itemQueryRepository.findItemsWithSalesMain();
		CachedMainItems cachedMainItems = cacheClient.getMainSalesItem();
		return cachedMainItems.getCachedItems();
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
		ItemPage pageDto
	) {

		CachedCategoryItems<ItemQueryDto> categoryItems =
			cacheClient.getCategoryItems(categoryName, keyword, priceFilter, pageDto);

		return categoryItems.toPage();
		// return itemQueryRepository.findItemsByCategory(categoryName, keyword, priceFilter, pageDto);
	}

	public Page<ItemQueryDto> findByBrand(String brand, ItemPage searchDto, PriceFilter priceFilter) {
		return itemQueryRepository.findByBrand(brand, searchDto, priceFilter);
	}
}
