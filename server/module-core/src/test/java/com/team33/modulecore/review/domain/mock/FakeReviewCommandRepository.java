package com.team33.modulecore.review.domain.mock;

import java.util.Optional;

import javax.persistence.EntityManager;

import com.team33.modulecore.core.review.domain.entity.Review;
import com.team33.modulecore.core.review.domain.repository.ReviewCommandRepository;

public class FakeReviewCommandRepository implements ReviewCommandRepository {
	private EntityManager em;

	public FakeReviewCommandRepository(EntityManager em) {
		this.em = em;
	}

	@Override
	public Review save(Review review) {
		return null;
	}

	@Override
	public Optional<Review> findById(Long reviewId) {
		return Optional.empty();
	}

	@Override
	public boolean findDuplicated(Long itemId, Long userId) {
		 return em.createQuery(
				"select count(r.id) > 0 from Review r where r.itemId = :itemId and r.userId = :userId",
						Boolean.class
			)
			.setParameter("itemId", itemId)
			.setParameter("userId", userId)
			.getSingleResult();

	}

}
