package com.team33.moduleapi.ui.review.dto;

import org.springframework.stereotype.Component;

import com.team33.modulecore.review.dto.query.ReviewPage;
import com.team33.modulecore.review.dto.query.ReviewSortOption;

@Component
public class ReviewPageMapper {
	private static final int DEFAULT_PAGE_SIZE = 16;
	private static final int MAX_SIZE = 2000;
	private static final int MIN_SIZE = 1;
	private static final ReviewSortOption DEFAULT_ITEM_SORT_OPTION = ReviewSortOption.NEWEST;

	public ReviewPage toReviewPage(ReviewPageDto dto) {
		return ReviewPage.builder()
			.page(Math.max(dto.getPage(), MIN_SIZE))
			.size(getSize(dto.getSize()))
			.sortOption(
				dto.getReviewSortOption() == ReviewSortOption.NEWEST
					? DEFAULT_ITEM_SORT_OPTION
					: dto.getReviewSortOption()
			)
			.build();
	}

	private int getSize(int size) {
		return size < DEFAULT_PAGE_SIZE
			? DEFAULT_PAGE_SIZE
			: Math.min(size, MAX_SIZE);
	}
}
