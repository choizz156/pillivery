package com.team33.modulecore.review.domain.repository;

import java.util.Optional;

import org.springframework.data.repository.Repository;

import com.team33.modulecore.review.domain.entity.Review;

public interface ReviewCommandRepository extends Repository<Review, Long> {

    Review save(Review review);
    Optional<Review> findById(Long reviewId);
}
