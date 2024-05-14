package com.team33.modulecore.review.mock;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import com.team33.modulecore.review.domain.Review;
import com.team33.modulecore.review.repository.ReviewCommandRepository;

public class FakeReviewRepository implements ReviewCommandRepository {

	private Map<Long, Review> store = new HashMap<>();

	@Override
	public Review save(Review review) {
		 store.put(review.getId(), review);
		 return review;
	}

	@Override
	public Optional<Review> findById(Long reviewId) {
		return Optional.ofNullable(store.get(reviewId));
	}
}
