package com.team33.modulecore.review.application;

import javax.transaction.Transactional;

import org.springframework.stereotype.Service;

import com.team33.modulecore.common.ItemFindHelper;
import com.team33.modulecore.common.UserFindHelper;
import com.team33.modulecore.exception.BusinessLogicException;
import com.team33.modulecore.exception.ExceptionCode;
import com.team33.modulecore.item.domain.entity.Item;
import com.team33.modulecore.review.domain.Review;
import com.team33.modulecore.review.domain.ReviewContext;
import com.team33.modulecore.review.repository.ReviewCommandRepository;
import com.team33.modulecore.user.domain.ReviewId;
import com.team33.modulecore.user.domain.User;

import lombok.RequiredArgsConstructor;

@Service
@Transactional
@RequiredArgsConstructor
public class ReviewService {

	private final ReviewCommandRepository reviewCommandRepository;
	private final ItemFindHelper itemFindHelper;
	private final UserFindHelper userFindHelper;

	public Review createReview(ReviewContext context) {
		Review review = Review.create(context);

		reviewCommandRepository.save(review);

		User user = userFindHelper.findUser(context.getUserId());
		user.addReviewId(review.getId());

		Item item = itemFindHelper.findItem(context.getItemId());
		item.addReviewId(review.getId());
		item.updateStars(context.getStar());

		return review;
	}

	public Review updateReview(Long reviewId, ReviewContext context) {
		Review review = findReview(reviewId);
		return review.update(context);
	}

	public void deleteReview(Long reviewId, ReviewContext context) {
		Review review = findReview(reviewId);
		review.delete(context);

		User user = userFindHelper.findUser(context.getUserId());
		user.getReviewIds().remove(new ReviewId(reviewId));

		Item item = itemFindHelper.findItem(context.getItemId());
		item.getReviewIds().remove(new ReviewId(reviewId));
		item.subtractReviewCount();
	}

	public Review findReview(long reviewId) {
		return reviewCommandRepository.findById(reviewId)
			.orElseThrow(() -> new BusinessLogicException(ExceptionCode.REVIEW_NOT_FOUND));
	}

	// public Page<Review> findReviews(User user, int page, int size, String sort) {
	//
	// 	Page<Review> pageReviews = reviewCommandRepository.findAllByUser(
	// 		PageRequest.of(page, size, Sort.by(sort).descending()), user);
	//
	// 	return pageReviews;
	// }
	//
	// public Page<Review> findItemReviews(Item item, int page, int size) {
	//
	// 	Page<Review> pageReview = reviewCommandRepository.findAllByItem(
	// 		PageRequest.of(page, size, Sort.by("reviewId").descending()), item);
	//
	// 	return pageReview;
	// }

	//    public Review updateReview(Review review) {
	//        Review findReview = findVerifiedReview(review.getReviewId());
	//
	//        Optional.ofNullable(review.getContent())
	//                .ifPresent(findReview::setContent);
	//
	//        Optional.ofNullable(review.getStar())
	//                .ifPresent(findReview::setStar);
	//
	//        Review updatedReview = reviewRepository.save(findReview);
	//        refreshStarAvg(findReview.getItem().getId());
	//        return updatedReview;
	//    }

	//    public void deleteReview(long reviewId, long userId) {
	//        Review review = findVerifiedReview(reviewId);
	//        long writerId = findReviewWriter(reviewId);
	//
	//        if(userId != writerId) {
	//            throw new BusinessLogicException(ExceptionCode.ACCESS_DENIED_USER);
	//        }
	//
	//        reviewRepository.delete(review);
	//        refreshStarAvg(review.getItem().getId());
	//    }

	// public double getStarAvg(long itemId) {
	// 	Optional<Double> optionalStarAvg = reviewCommandRepository.findReviewAvg(itemId);
	// 	double starAvg = optionalStarAvg.orElse((double)0);
	// 	return starAvg;
	// }
}
