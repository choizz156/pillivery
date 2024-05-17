package com.team33.modulecore.item.application;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.team33.modulecore.category.domain.CategoryName;
import com.team33.modulecore.item.domain.entity.Item;
import com.team33.modulecore.item.domain.repository.ItemQueryRepository;
import com.team33.modulecore.item.dto.query.ItemPage;
import com.team33.modulecore.item.dto.query.ItemQueryDto;
import com.team33.modulecore.item.dto.query.PriceFilter;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class ItemQueryService {

	private final ItemQueryRepository itemQueryRepository;

	public List<ItemQueryDto> findMainDiscountItems() {
		return itemQueryRepository.findItemsWithDiscountRateMain();
	}

	public List<ItemQueryDto> findMainSaleItems() {
		return itemQueryRepository.findItemsWithSalesMain();
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

	public Page<ItemQueryDto> findByBrand(String keyword, ItemPage searchDto, PriceFilter priceFilter) {
		return itemQueryRepository.findByBrand(keyword, searchDto, priceFilter);
	}
}
