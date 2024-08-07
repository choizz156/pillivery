package com.team33.modulecore.core.review.infra;

import static com.team33.modulecore.core.review.domain.entity.QReview.*;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.stereotype.Repository;

import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.team33.modulecore.core.review.domain.ReviewStatus;
import com.team33.modulecore.core.review.domain.repository.ReviewQueryRepository;
import com.team33.modulecore.core.review.dto.query.QReviewQueryDto;
import com.team33.modulecore.core.review.dto.query.ReviewPage;
import com.team33.modulecore.core.review.dto.query.ReviewQueryDto;
import com.team33.modulecore.core.review.dto.query.ReviewSortOption;
import com.team33.modulecore.exception.BusinessLogicException;
import com.team33.modulecore.exception.ExceptionCode;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Repository
public class ReviewQueryDslDao implements ReviewQueryRepository {

	private final JPAQueryFactory queryFactory;

	@Override
	public ReviewQueryDto findById(Long reviewId) {
		ReviewQueryDto reviewQueryDto = selectReviewQeuryDtoFromReview()
			.where(
				review.id.eq(reviewId),
				StatusActiveEqActive()
			)
			.fetchFirst();

		if (reviewQueryDto == null) {
			throw new BusinessLogicException(ExceptionCode.REVIEW_NOT_FOUND);
		}

		return reviewQueryDto;
	}

	@Override
	public Page<ReviewQueryDto> findAllByItemId(long itemId, ReviewPage reviewPage) {
		BooleanExpression statusActive = StatusActiveEqActive();
		BooleanExpression itemIdEq = itemIdEq(itemId);

		List<ReviewQueryDto> fetch = selectReviewQeuryDtoFromReview()
			.where(
				itemIdEq,
				statusActive
			)
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
			.where(
				itemIdEq,
				statusActive
			);

		return PageableExecutionUtils.getPage(
			fetch,
			PageRequest.of(reviewPage.getPage() - 1, reviewPage.getSize()),
			count::fetchOne
		);
	}

	private static BooleanExpression itemIdEq(long itemId) {
		return review.itemId.eq(itemId);
	}

	@Override
	public Page<ReviewQueryDto> findAllByUserId(long userId, ReviewPage reviewPage) {
		BooleanExpression statusActive = StatusActiveEqActive();

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
			.where(
				review.userId.eq(userId),
				statusActive
			)
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
			.where(
				review.userId.eq(userId),
				statusActive
			);

		return PageableExecutionUtils.getPage(
			fetch,
			PageRequest.of(reviewPage.getPage() - 1, reviewPage.getSize()),
			count::fetchOne
		);
	}

	private BooleanExpression StatusActiveEqActive() {
		return review.reviewStatus.eq(ReviewStatus.ACTIVE);
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
