package com.team33.modulecore.core.item.application;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.team33.modulecore.cache.CacheClient;
import com.team33.modulecore.core.category.domain.CategoryName;
import com.team33.modulecore.core.item.dto.query.ItemQueryDto;
import com.team33.modulecore.core.item.dto.query.PriceFilter;
import com.team33.modulecore.core.item.domain.repository.ItemQueryRepository;
import com.team33.modulecore.core.item.dto.query.ItemPage;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class ItemQueryService {

	private final CacheClient cacheClient;
	private final ItemQueryRepository itemQueryRepository;

	public List<ItemQueryDto> findMainDiscountItems() {
		return cacheClient.getMainDiscountItem();
	}

	public List<ItemQueryDto> findMainSaleItems() {
		return cacheClient.getMainSalesItem();
	}

	public Page<ItemQueryDto> findFilteredItem(
		String keyword,
		PriceFilter priceFilter,
		ItemPage pageDto
	) {
		return 	itemQueryRepository.findFilteredItems(keyword, priceFilter, pageDto);
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
		return itemQueryRepository.findItemsByCategory(categoryName, keyword, priceFilter, pageDto);
	}

	public Page<ItemQueryDto> findByBrand(String brand, ItemPage searchDto, PriceFilter priceFilter) {
		return itemQueryRepository.findByBrand(brand, searchDto, priceFilter);
	}
}
