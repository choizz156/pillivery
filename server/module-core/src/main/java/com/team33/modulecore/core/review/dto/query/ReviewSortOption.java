package com.team33.modulecore.core.review.dto.query;



import static com.team33.modulecore.core.review.domain.entity.QReview.*;

import com.querydsl.core.types.OrderSpecifier;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ReviewSortOption {

	NEWEST(review.createdAt.desc(), "최신순"),
	OLDEST(review.createdAt.asc(), "오래된 순"),
	STAR_L(review.star.asc(), "별점 낮은 순"),
	STAR_H(review.star.desc(), "별점 높은 순");

	private final OrderSpecifier<? extends Comparable<?>> sort;
	private final String description;
}
