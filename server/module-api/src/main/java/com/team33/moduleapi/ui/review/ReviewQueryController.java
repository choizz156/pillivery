package com.team33.moduleapi.ui.review;

import org.springframework.data.domain.Page;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.team33.moduleapi.response.MultiResponseDto;
import com.team33.moduleapi.response.SingleResponseDto;
import com.team33.moduleapi.ui.review.mapper.ReviewPageMapper;
import com.team33.modulecore.core.review.domain.repository.ReviewQueryRepository;
import com.team33.modulecore.core.review.dto.query.ReviewPage;
import com.team33.modulecore.core.review.dto.query.ReviewQueryDto;
import com.team33.modulecore.core.review.dto.query.ReviewSortOption;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping( "/api/reviews")
public class ReviewQueryController {

	private final ReviewQueryRepository reviewQueryRepository;
	private final ReviewPageMapper reviewPageMapper;

	@GetMapping("/{reviewId}")
	public SingleResponseDto<ReviewQueryDto> getReview(
		@PathVariable("reviewId") Long reviewId
	) {
		ReviewQueryDto reviewQueryDto = reviewQueryRepository.findById(reviewId);

		return new SingleResponseDto<>(reviewQueryDto);
	}

	@GetMapping("/api/items/{itemId}")
	public MultiResponseDto<?> getReviewByItemId(
		@PathVariable long itemId,
		@RequestParam(defaultValue = "1") int page,
		@RequestParam(defaultValue = "8") int size,
		@RequestParam(defaultValue = "NEWEST") ReviewSortOption sort
	) {

		ReviewPage reviewPage = reviewPageMapper.toReviewPage(page, size, sort);

		Page<ReviewQueryDto> reviewsPage = reviewQueryRepository.findAllByItemId(itemId, reviewPage);

		return new MultiResponseDto<>(reviewsPage.getContent(), reviewsPage);
	}

	@GetMapping("/api/users/{userId}")
	public MultiResponseDto<ReviewQueryDto> getReviewByUserId(
		@PathVariable long userId,
		@RequestParam(defaultValue = "1") int page,
		@RequestParam(defaultValue = "8") int size,
		@RequestParam(defaultValue = "NEWEST") ReviewSortOption sort
	) {
		ReviewPage reviewPage = reviewPageMapper.toReviewPage(page, size, sort);
		Page<ReviewQueryDto> reviewsPage = reviewQueryRepository.findAllByUserId(userId, reviewPage);

		return new MultiResponseDto<>(reviewsPage.getContent(), reviewsPage);
	}
}
