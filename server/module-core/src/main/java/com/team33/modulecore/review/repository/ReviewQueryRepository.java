package com.team33.modulecore.review.repository;

import org.springframework.data.domain.Page;
import org.springframework.stereotype.Repository;

import com.team33.modulecore.review.dto.query.ReviewPage;
import com.team33.modulecore.review.dto.query.ReviewQueryDto;

@Repository
public interface ReviewQueryRepository {
	ReviewQueryDto findById(Long reviewId);
	Page<ReviewQueryDto> findAllByItemId(Long itemId, ReviewPage reviewPage);
	Page<ReviewQueryDto> findAllByUserId(Long userId, ReviewPage reviewPage);

}
