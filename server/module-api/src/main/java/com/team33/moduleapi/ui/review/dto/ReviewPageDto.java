package com.team33.moduleapi.ui.review.dto;

import com.team33.modulecore.core.review.dto.query.ReviewSortOption;

import lombok.Data;

@Data
public class ReviewPageDto {
	public int size;
	public int page;
	public ReviewSortOption reviewSortOption = ReviewSortOption.NEWEST;
}
