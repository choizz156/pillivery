package com.team33.modulecore.review.application;

import javax.transaction.Transactional;

import org.springframework.stereotype.Service;

import com.team33.modulecore.exception.BusinessLogicException;
import com.team33.modulecore.exception.ExceptionCode;
import com.team33.modulecore.item.application.ItemCommandService;
import com.team33.modulecore.review.domain.ReviewContext;
import com.team33.modulecore.review.domain.entity.Review;
import com.team33.modulecore.review.domain.repository.ReviewCommandRepository;
import com.team33.modulecore.user.application.UserService;

import lombok.RequiredArgsConstructor;

@Service
@Transactional
@RequiredArgsConstructor
public class ReviewCommandService {

	private final ReviewCommandRepository reviewCommandRepository;
	private final UserService userService;
	private final ItemCommandService itemCommandService;

	public Review createReview(ReviewContext context) {
		checkDuplicateReview(context);

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
		Long reviewId = context.getReviewId();

		Review review = findReview(reviewId);
		review.delete(context);

		userService.deleteReviewId(context.getUserId(), review.getId());
		itemCommandService.deleteReviewId(context.getItemId(), review);

	}

	public Review findReview(long reviewId) {
		return reviewCommandRepository.findById(reviewId)
			.orElseThrow(() -> new BusinessLogicException(ExceptionCode.REVIEW_NOT_FOUND));
	}

	private void checkDuplicateReview(ReviewContext context) {
		boolean duplicated = reviewCommandRepository.findDuplicated(context.getItemId(), context.getUserId());
		if(duplicated){
			throw new BusinessLogicException(ExceptionCode.DUPLICATED_REVIEW);
		}
	}
}
