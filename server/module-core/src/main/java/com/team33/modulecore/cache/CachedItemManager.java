package com.team33.modulecore.cache;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

import com.team33.modulecore.cache.dto.CachedCategoryItems;
import com.team33.modulecore.cache.dto.CachedItems;
import com.team33.modulecore.core.category.domain.CategoryName;
import com.team33.modulecore.core.item.domain.repository.ItemQueryRepository;
import com.team33.modulecore.core.item.dto.query.ItemPage;
import com.team33.modulecore.core.item.dto.query.ItemQueryDto;
import com.team33.modulecore.core.item.dto.query.PriceFilter;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Component
public class CachedItemManager {

	private final static Logger LOGGER = LoggerFactory.getLogger("fileLog");

	private final ItemQueryRepository itemQueryRepository;

	@Cacheable(value = "MAIN_ITEMS", key = "'discount'")
	public CachedItems<ItemQueryDto> getMainDiscountItem() {
		LOGGER.info("[Cache miss] mainItems - discount");
		List<ItemQueryDto> mainItem = itemQueryRepository.findItemsWithDiscountRateMain();
		return CachedItems.of(mainItem);
	}

	@Cacheable(value = "MAIN_ITEMS", key = "'sales'")
	public CachedItems<ItemQueryDto> getMainSalesItem() {
		LOGGER.info("[Cache miss] mainItems - sales");
		List<ItemQueryDto> mainItem = itemQueryRepository.findItemsWithSalesMain();
		return CachedItems.of(mainItem);
	}

	@Cacheable(value = "CATEGORY_ITEMS", key = "#categoryName.name() + '-' + #itemPage.page", condition = "#itemPage.page <= 5")
	public CachedCategoryItems<ItemQueryDto> getCategoryItems(
		CategoryName categoryName,
		String keyword,
		PriceFilter priceFilter,
		ItemPage itemPage) {

		LOGGER.info("[Cache miss] Category: " + categoryName.name());
		Page<ItemQueryDto> items = itemQueryRepository.findItemsByCategory(
			categoryName,
			keyword,
			priceFilter,
			itemPage);

		return new CachedCategoryItems<>(items);
	}
}
