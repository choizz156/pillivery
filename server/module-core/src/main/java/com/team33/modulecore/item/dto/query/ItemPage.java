package com.team33.modulecore.item.dto.query;

import org.springframework.data.domain.Sort.Direction;

import com.team33.modulecore.item.domain.ItemSortOption;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@NoArgsConstructor
@Getter
public class ItemPage {
	private int page;
	private int size;
	@Builder.Default
	private Direction sort = Direction.DESC;
	@Builder.Default
	private ItemSortOption sortOption = ItemSortOption.SALES;

	private ItemPage(int page, int size, Direction sort, ItemSortOption sortOption) {
		this.page = page;
		this.size = size;
		this.sort = sort;
		this.sortOption = sortOption;
	}

	public long getOffset() {
		return ((long)(this.page - 1) * this.size);
	}
}

