package com.team33.moduleapi.api.review.mapper;

import org.springframework.stereotype.Component;

import com.team33.modulecore.core.review.dto.query.ReviewPage;
import com.team33.modulecore.core.review.dto.query.ReviewSortOption;

@Component
public class ReviewPageMapper {
	private static final int DEFAULT_PAGE_SIZE = 8;
	private static final int MAX_SIZE = 2000;
	private static final int MIN_SIZE = 1;
	private static final ReviewSortOption DEFAULT_ITEM_SORT_OPTION = ReviewSortOption.NEWEST;

	public ReviewPage toReviewPage(int page, int size, ReviewSortOption sort) {
	 return ReviewPage.builder()
		 .page(Math.max(page, MIN_SIZE))
		 .size(getSize(size))
		 .sortOption(
			 sort == ReviewSortOption.NEWEST || sort == null
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
