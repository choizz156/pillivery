package com.team33.modulecore.domain.review.service;

import com.team33.modulecore.domain.review.entity.Review;
import com.team33.modulecore.domain.review.repository.ReviewRepository;
import com.team33.modulecore.global.exception.BusinessLogicException;
import com.team33.modulecore.global.exception.ExceptionCode;
import java.util.Optional;
import javax.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import com.team33.modulecore.domain.item.entity.Item;
import com.team33.modulecore.domain.item.repository.ItemRepository;
import com.team33.modulecore.domain.item.service.ItemService;
import com.team33.modulecore.domain.user.entity.User;


@Service
@Transactional
@RequiredArgsConstructor
public class ReviewService {
    private final ItemRepository itemRepository;

    private final ReviewRepository reviewRepository;
    private final ItemService itemService;

    public Review createReview(Review review) {
        reviewRepository.save(review);
        refreshStarAvg(review.getItem().getItemId());
        return review;
    }

    public Review findReview(long reviewId) {
        Review review = findVerifiedReview(reviewId);
        reviewRepository.save(review);
        return review;
    }

    public Page<Review> findReviews(User user, int page, int size, String sort) {

        Page<Review> pageReviews = reviewRepository.findAllByUser(
                PageRequest.of(page, size, Sort.by(sort).descending()), user);

        return pageReviews;
    }

    public Page<Review> findItemReviews(Item item, int page, int size) {

        Page<Review> pageReview = reviewRepository.findAllByItem(
                PageRequest.of(page, size, Sort.by("reviewId").descending()), item);

        return pageReview;
    }

    public long findReviewWriter(long reviewId) { // 작성자만 수정, 삭제를 할 수 있도록 리뷰의 작성자 찾기
        Review review = findVerifiedReview(reviewId);
        return review.getUser().getUserId();
    }

    public Review updateReview(Review review) {
        Review findReview = findVerifiedReview(review.getReviewId());

        Optional.ofNullable(review.getContent())
                .ifPresent(findReview::setContent);

        Optional.ofNullable(review.getStar())
                .ifPresent(findReview::setStar);

        Review updatedReview = reviewRepository.save(findReview);
        refreshStarAvg(findReview.getItem().getItemId());
        return updatedReview;
    }

    public Review findVerifiedReview(long reviewId) {
        Optional<Review> optionalReview = reviewRepository.findById(reviewId);
        Review findReview = optionalReview.orElseThrow(
                () -> new BusinessLogicException(ExceptionCode.REVIEW_NOT_FOUND));

        return findReview;
    }

    public void deleteReview(long reviewId, long userId) {
        Review review = findVerifiedReview(reviewId);
        long writerId = findReviewWriter(reviewId);

        if(userId != writerId) {
            throw new BusinessLogicException(ExceptionCode.ACCESS_DENIED_USER);
        }

        reviewRepository.delete(review);
        refreshStarAvg(review.getItem().getItemId());
    }

    public double getStarAvg(long itemId) {
        Optional<Double> optionalStarAvg = reviewRepository.findReviewAvg(itemId);
        double starAvg = optionalStarAvg.orElse((double)0);
        return starAvg;
    }


    // 리뷰 등록, 수정, 삭제 하는 경우 평균 별점을 갱신하는 로직
    public void refreshStarAvg(long itemId) {
        Item item = itemService.findVerifiedItem(itemId);
        item.setStarAvg(getStarAvg(itemId));
        itemRepository.save(item);
    }

}
