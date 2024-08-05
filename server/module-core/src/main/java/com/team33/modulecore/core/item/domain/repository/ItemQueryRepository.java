package com.team33.modulecore.core.item.domain.repository;

import java.util.List;

import org.springframework.data.domain.Page;

import com.team33.modulecore.core.category.domain.CategoryName;
import com.team33.modulecore.core.item.dto.query.ItemQueryDto;
import com.team33.modulecore.core.item.dto.query.PriceFilter;
import com.team33.modulecore.core.item.domain.entity.Item;
import com.team33.modulecore.core.item.dto.query.ItemPage;

public interface ItemQueryRepository {

	Item findById(long id);

	List<ItemQueryDto> findItemsWithSalesMain();

	List<ItemQueryDto> findItemsWithDiscountRateMain();

	Page<ItemQueryDto> findFilteredItems(
		String keyword,
		PriceFilter priceFilter,
		ItemPage pageDto
	);

	Page<ItemQueryDto> findItemsOnSale(String keyword,  PriceFilter priceFilter, ItemPage pageDto);

	Page<ItemQueryDto> findItemsByCategory(
		CategoryName categoryName,
		String keyword,
		PriceFilter priceFilter,
		ItemPage pageDto
	);

	Page<ItemQueryDto> findByBrand(String keyword, ItemPage searchDto, PriceFilter priceFilter);
}
