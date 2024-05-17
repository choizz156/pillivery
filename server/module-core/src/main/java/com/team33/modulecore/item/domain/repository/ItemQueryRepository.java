package com.team33.modulecore.item.domain.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.stereotype.Repository;

import com.team33.modulecore.category.domain.CategoryName;
import com.team33.modulecore.item.domain.entity.Item;
import com.team33.modulecore.item.dto.query.ItemPage;
import com.team33.modulecore.item.dto.query.ItemQueryDto;
import com.team33.modulecore.item.dto.query.PriceFilter;

@Repository
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
