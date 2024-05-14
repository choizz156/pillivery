package com.team33.moduleapi.ui.review.dto;

import org.springframework.data.domain.Page;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.team33.moduleapi.dto.MultiResponseDto;
import com.team33.modulecore.review.dto.query.ReviewPage;
import com.team33.modulecore.review.dto.query.ReviewQueryDto;
import com.team33.modulecore.review.repository.ReviewQueryRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping(value = "/reviews")
public class ReviewQueryController {

	private final ReviewQueryRepository reviewQueryRepository;
	private final ReviewPageMapper reviewPageMapper;

	@GetMapping("/{reviewId}")
	public ReviewQueryDto getReview(
		@PathVariable("reviewId") Long reviewId
	) {
		return reviewQueryRepository.findById(reviewId);
	}

	@GetMapping
	public MultiResponseDto<ReviewQueryDto> getReviewByItemId(
		@RequestParam("itemId") Long itemId,
		ReviewPageDto reviewPageDto
	) {
		ReviewPage reviewPage = reviewPageMapper.toReviewPage(reviewPageDto);
		Page<ReviewQueryDto> reviewsPage = reviewQueryRepository.findAllByItemId(itemId, reviewPage);

		return new MultiResponseDto<>(reviewsPage.getContent(), reviewsPage);
	}

	@GetMapping
	public MultiResponseDto<ReviewQueryDto> getReviewByUserId(
		@RequestParam("userId") Long userId,
		ReviewPageDto reviewPageDto
	) {
		ReviewPage reviewPage = reviewPageMapper.toReviewPage(reviewPageDto);
		Page<ReviewQueryDto> reviewsPage = reviewQueryRepository.findAllByUserId(userId, reviewPage);

		return new MultiResponseDto<>(reviewsPage.getContent(), reviewsPage);
	}
}
