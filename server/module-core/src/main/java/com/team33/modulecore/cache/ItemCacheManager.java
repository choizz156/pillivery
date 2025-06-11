package com.team33.modulecore.cache;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;

import com.team33.modulecore.cache.dto.CachedCategoryItems;
import com.team33.modulecore.cache.dto.CachedItems;
import com.team33.modulecore.core.category.domain.CategoryName;
import com.team33.modulecore.core.item.dto.query.ItemPage;
import com.team33.modulecore.core.item.dto.query.ItemQueryDto;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Component
public class ItemCacheManager {

	private final static Logger LOGGER = LoggerFactory.getLogger("fileLog");

	@Cacheable(value = "MAIN_ITEMS", key = "'discount'")
	public CachedItems<ItemQueryDto> getMainDiscountItem() {

		LOGGER.debug("Get main discount item");
		return new CachedItems<>();
	}

	@Cacheable(value = "MAIN_ITEMS", key = "'sales'")
	public CachedItems<ItemQueryDto> getMainSalesItem() {

		LOGGER.debug("Get main sales item");
		return new CachedItems<>();
	}

	@Cacheable(value = "CATEGORY_ITEMS", key = "#categoryName.name() + '-' + #itemPage.page+ '-' + 'SALES'", condition = "#itemPage.page <= 5")
	public CachedCategoryItems<ItemQueryDto> getCategoryItemsOnSales(
		CategoryName categoryName,
		ItemPage itemPage
	) {

		throw new IllegalStateException();
	}

	@Cacheable(value = "CATEGORY_ITEMS", key = "#categoryName.name() + '-' + #itemPage.page+ '-' + 'DISCOUNT_RATE_L'", condition = "#itemPage.page <= 5")
	public CachedCategoryItems<ItemQueryDto> getCategoryItemsOnDiscount(
		CategoryName categoryName,
		ItemPage itemPage
	) {

		throw new IllegalStateException();
	}
}
