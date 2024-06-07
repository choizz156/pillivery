package com.team33.moduleapi.ui.item.mapper;

import static com.team33.modulecore.item.domain.ItemSortOption.*;

import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;

import com.team33.moduleapi.ui.item.dto.ItemPageRequestDto;
import com.team33.modulecore.item.domain.ItemSortOption;
import com.team33.modulecore.item.dto.query.ItemPage;
import com.team33.modulecore.item.dto.query.PriceFilter;

@Component
public class ItemQueryServiceMapper {

	private static final int DEFAULT_PAGE_SIZE = 16;
	private static final int MAX_SIZE = 2000;
	private static final int MIN_SIZE = 1;
	private static final ItemSortOption DEFAULT_ITEM_SORT_OPTION = SALES;
	private static final Sort.Direction DEFAULT_SORT_TYPE = Sort.Direction.DESC;

	public PriceFilter toPriceFilterDto(int low, int high) {
		return new PriceFilter(low, high);
	}

	public ItemPage toItemPageDto(ItemPageRequestDto dto) {
		return ItemPage.builder()
			.page(Math.max(dto.getPage(), MIN_SIZE))
			.size(getSize(dto.getSize()))
			.sort(dto.getDirection() == Sort.Direction.ASC ? Sort.Direction.ASC : DEFAULT_SORT_TYPE)
			.sortOption(
				dto.getSortOption() == SALES
					? DEFAULT_ITEM_SORT_OPTION
					: dto.getSortOption())
			.build();
	}

	private int getSize(int size) {
		return size < DEFAULT_PAGE_SIZE
			? DEFAULT_PAGE_SIZE
			: Math.min(size, MAX_SIZE);
	}
}
