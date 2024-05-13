package com.team33.modulecore.review.dto.query;

import org.springframework.data.domain.Sort.Direction;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@NoArgsConstructor
@Getter
public class ReviewPage {
	private int page;
	private int size;
	@Builder.Default
	private Direction sort = Direction.DESC;
	@Builder.Default
	private ReviewSortOption sortOption = ReviewSortOption.NEWEST;

	private ReviewPage(int page, int size, Direction sort, ReviewSortOption sortOption) {
		this.page = page;
		this.size = size;
		this.sort = sort;
		this.sortOption = sortOption;
	}

	public long getOffset() {
		return ((long)(this.page - 1) * this.size);
	}
}

