package com.team33.modulecore.cache;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

import com.team33.modulecore.cache.dto.CachedCategoryItems;
import com.team33.modulecore.cache.dto.CachedItems;
import com.team33.modulecore.core.category.domain.CategoryName;
import com.team33.modulecore.core.item.domain.ItemSortOption;
import com.team33.modulecore.core.item.domain.repository.ItemQueryRepository;
import com.team33.modulecore.core.item.dto.query.ItemPage;
import com.team33.modulecore.core.item.dto.query.ItemQueryDto;
import com.team33.modulecore.core.item.dto.query.PriceFilter;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Component
public class ItemCacheLoader {

	private final static Logger LOGGER = LoggerFactory.getLogger("fileLog");

	private final ItemQueryRepository itemQueryRepository;

	public CachedCategoryItems<ItemQueryDto> loadCategoryItemsFromDB(Object keyObject) {

		String key = (String)keyObject;

		String[] parts = key.split("-");
		String categoryName = parts[0];
		int itemPage = Integer.parseInt(parts[1]);
		String sortOption = parts[2];

		LOGGER.info("[Cache miss] Category: {}", key);

		Page<ItemQueryDto> itemsByCategory = itemQueryRepository.findItemsByCategory(
			CategoryName.valueOf(categoryName),
			"",
			new PriceFilter(),
			ItemPage.builder()
				.page(itemPage)
				.sortOption(ItemSortOption.valueOf(sortOption))
				.build()
		);

		return new CachedCategoryItems<>(itemsByCategory);
	}

	public CachedItems<ItemQueryDto> loadMainItemsFromDB(Object keyObject) {

		String key = (String)keyObject;

		if (key.equals("sales")) {
			LOGGER.info("[Cache miss] mainItems - sales");

			List<ItemQueryDto> itemsWithSalesMain = itemQueryRepository.findItemsWithSalesMain();
			return CachedItems.of(itemsWithSalesMain);
		}

		LOGGER.info("[Cache miss] mainItems - discount");
		List<ItemQueryDto> itemsWithDiscountRateMain = itemQueryRepository.findItemsWithDiscountRateMain();
		return CachedItems.of(itemsWithDiscountRateMain);
	}

}
