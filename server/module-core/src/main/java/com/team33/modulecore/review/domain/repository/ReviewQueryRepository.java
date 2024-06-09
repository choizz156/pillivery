package com.team33.modulecore.review.domain.repository;

import org.springframework.data.domain.Page;

import com.team33.modulecore.review.dto.query.ReviewPage;
import com.team33.modulecore.review.dto.query.ReviewQueryDto;

public interface ReviewQueryRepository {
	ReviewQueryDto findById(Long reviewId);
	Page<ReviewQueryDto> findAllByItemId(long itemId, ReviewPage reviewPage);
	Page<ReviewQueryDto> findAllByUserId(long userId, ReviewPage reviewPage);

}
