package com.team33.modulecore.review.dto.query;

import com.querydsl.core.types.OrderSpecifier;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ReviewSortOption {

	NEWEST(null, "최신순"),
	OLDEST(null, "오래된 순"),
	STAR_ASC(null, "별점 낮은 순"),
	STAR_DESC(null, "별점 높은 순");

	private final OrderSpecifier<? extends Number> sort;
	private final String description;
}
