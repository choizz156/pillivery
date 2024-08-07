package com.team33.modulecore.core.review.application;

import javax.transaction.Transactional;

import org.springframework.stereotype.Service;

import com.team33.modulecore.exception.BusinessLogicException;
import com.team33.modulecore.exception.ExceptionCode;
import com.team33.modulecore.core.item.application.ItemCommandService;
import com.team33.modulecore.core.review.domain.ReviewContext;
import com.team33.modulecore.core.review.domain.entity.Review;
import com.team33.modulecore.core.review.domain.repository.ReviewCommandRepository;
import com.team33.modulecore.core.user.application.UserService;

import lombok.RequiredArgsConstructor;

@Service
@Transactional
@RequiredArgsConstructor
public class ReviewCommandService {

	private final ReviewCommandRepository reviewCommandRepository;
	private final ItemCommandService itemCommandService;
	private final UserService userService;

	public Review createReview(ReviewContext context) {

		Review review = reviewCommandRepository.save(Review.create(context));

		userService.addReviewId(context.getUserId(), review.getId());
		itemCommandService.addReviewId(context.getItemId(), review.getId(), context.getStar());

		return review;
	}

	public Review updateReview(ReviewContext context) {
		Review review = findReview(context.getReviewId());
		return review.update(context);
	}

	public void deleteReview(ReviewContext context) {
		long reviewId = context.getReviewId();

		Review review = findReview(reviewId);
		review.delete(context);

		userService.deleteReviewId(context.getUserId(), review.getId());
		itemCommandService.deleteReviewId(context.getItemId(), review);

	}

	public Review findReview(long reviewId) {
		return reviewCommandRepository.findById(reviewId)
			.orElseThrow(() -> new BusinessLogicException(ExceptionCode.REVIEW_NOT_FOUND));
	}

}
