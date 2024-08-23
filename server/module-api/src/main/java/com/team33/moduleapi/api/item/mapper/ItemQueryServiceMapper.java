package com.team33.moduleapi.api.item.mapper;

import static com.team33.modulecore.core.item.domain.ItemSortOption.*;

import org.springframework.stereotype.Component;

import com.team33.modulecore.core.item.domain.ItemSortOption;
import com.team33.modulecore.core.item.dto.query.ItemPage;
import com.team33.modulecore.core.item.dto.query.PriceFilter;

@Component
public class ItemQueryServiceMapper {

	private static final int DEFAULT_PAGE_SIZE = 8;
	private static final int MAX_SIZE = 2000;
	private static final int MIN_SIZE = 1;
	private static final ItemSortOption DEFAULT_ITEM_SORT_OPTION = SALES;

	public PriceFilter toPriceFilterDto(int low, int high) {
		return new PriceFilter(low, high);
	}

	public ItemPage toItemPageDto(int page, int size, ItemSortOption sort) {
		return ItemPage.builder()
			.page(Math.max(page, MIN_SIZE))
			.size(getSize(size))
			.sortOption(
				sort == SALES
					? DEFAULT_ITEM_SORT_OPTION
					: sort
			)
			.build();
	}

	private int getSize(int size) {
		return size < DEFAULT_PAGE_SIZE
			? DEFAULT_PAGE_SIZE
			: Math.min(size, MAX_SIZE);
	}
}
