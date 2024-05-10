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

	public List<com.team33.modulecore.item.dto.query.ItemQueryDto> findMainDiscountItems() {
		return itemQueryRepository.findItemsWithDiscountRateTop9();
	}

	public List<com.team33.modulecore.item.dto.query.ItemQueryDto> findMainSaleItems() {
		return itemQueryRepository.findItemsWithSalesTop9();
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

	public Page<com.team33.modulecore.item.dto.query.ItemQueryDto> findByCategory(
		CategoryName categoryName,
		String keyword,
		PriceFilter priceFilter,
		ItemPage pageDto
	) {
		return itemQueryRepository.findItemsByCategory(categoryName, keyword, priceFilter, pageDto);
	}

	public Item findItem(Long itemId) {
		return itemQueryRepository.findById(itemId);
	}
}
