package com.team33.modulecore.review.repository;

import org.springframework.data.domain.Page;
import org.springframework.stereotype.Repository;

import com.team33.modulecore.review.domain.entity.Review;
import com.team33.modulecore.review.dto.query.ReviewPage;
import com.team33.modulecore.review.dto.query.ReviewQueryDto;

@Repository
public interface ReviewQueryRepository {
	Review findById(Long reviewId);
	Page<ReviewQueryDto> findByItemId(Long itemId, ReviewPage reviewPage);
	Page<ReviewQueryDto> findByUserId(Long userId, ReviewPage reviewPage);

}
