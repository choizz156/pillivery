package com.team33.modulecore.core.review.domain.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.Repository;

import com.team33.modulecore.core.review.domain.entity.Review;

public interface ReviewCommandRepository extends Repository<Review, Long> {

    Review save(Review review);
    Optional<Review> findById(Long reviewId);

	@Query("select count(r.id) > 0 from Review r where r.itemId = :itemId and r.userId = :userId")
	boolean findDuplicated(Long itemId, Long userId);
}
