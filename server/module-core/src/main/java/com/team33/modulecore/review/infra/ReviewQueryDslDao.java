package com.team33.modulecore.review.infra;

import static com.team33.modulecore.review.domain.entity.QReview.*;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.support.PageableExecutionUtils;

import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.team33.modulecore.exception.BusinessLogicException;
import com.team33.modulecore.exception.ExceptionCode;
import com.team33.modulecore.review.dto.query.QReviewQueryDto;
import com.team33.modulecore.review.dto.query.ReviewPage;
import com.team33.modulecore.review.dto.query.ReviewQueryDto;
import com.team33.modulecore.review.dto.query.ReviewSortOption;
import com.team33.modulecore.review.repository.ReviewQueryRepository;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class ReviewQueryDslDao implements ReviewQueryRepository {
	private final JPAQueryFactory queryFactory;

	@Override
	public ReviewQueryDto findById(Long reviewId) {
		ReviewQueryDto reviewQueryDto = selectReviewQeuryDtoFromReview()
			.where(review.id.eq(reviewId))
			.fetchFirst();

		if (reviewQueryDto == null) {
			throw new BusinessLogicException(ExceptionCode.REVIEW_NOT_FOUND);
		}

		return reviewQueryDto;
	}

	@Override
	public Page<ReviewQueryDto> findAllByItemId(Long itemId, ReviewPage reviewPage) {
		List<ReviewQueryDto> fetch = selectReviewQeuryDtoFromReview()
			.where(review.itemId.eq(itemId))
			.orderBy(getOrderSort(reviewPage.getSortOption()))
			.offset(reviewPage.getOffset())
			.limit(reviewPage.getSize())
			.fetch();

		if (fetch.isEmpty()) {
			return Page.empty();
		}

		JPAQuery<Long> count = queryFactory.
			select(review.count())
			.from(review)
			.where(review.itemId.eq(itemId));

		return PageableExecutionUtils.getPage(
			fetch,
			PageRequest.of(reviewPage.getPage() - 1, reviewPage.getSize()),
			count::fetchOne
		);
	}

	@Override
	public Page<ReviewQueryDto> findAllByUserId(Long userId, ReviewPage reviewPage) {
		List<ReviewQueryDto> fetch = queryFactory.select(new QReviewQueryDto(
					review.id,
					review.itemId,
					review.userId,
					review.content,
					review.displayName,
					review.star,
					review.createdAt,
					review.updatedAt,
					review.reviewStatus
				)
			)
			.from(review)
			.where(review.userId.eq(userId))
			.orderBy(getOrderSort(reviewPage.getSortOption()))
			.offset(reviewPage.getOffset())
			.limit(reviewPage.getSize())
			.fetch();

		if (fetch.isEmpty()) {
			return Page.empty();
		}

		JPAQuery<Long> count = queryFactory.
			select(review.count())
			.from(review)
			.where(review.userId.eq(userId));

		return PageableExecutionUtils.getPage(
			fetch,
			PageRequest.of(reviewPage.getPage() - 1, reviewPage.getSize()),
			count::fetchOne
		);
	}

	private JPAQuery<ReviewQueryDto> selectReviewQeuryDtoFromReview() {
		return queryFactory.select(new QReviewQueryDto(
					review.id,
					review.itemId,
					review.userId,
					review.content,
					review.displayName,
					review.star,
					review.createdAt,
					review.updatedAt,
					review.reviewStatus
				)
			)
			.from(review);
	}

	@SuppressWarnings("unchecked")
	private <T extends Comparable<T>> OrderSpecifier<T> getOrderSort(ReviewSortOption reviewSortOption) {
		return (OrderSpecifier<T>)reviewSortOption.getSort();
	}
}
