package com.team33.moduleapi.api.review;

import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.team33.moduleapi.response.SingleResponseDto;
import com.team33.moduleapi.api.review.dto.ReviewDeleteDto;
import com.team33.moduleapi.api.review.dto.ReviewDetailResponseDto;
import com.team33.moduleapi.api.review.dto.ReviewPatchDto;
import com.team33.moduleapi.api.review.dto.ReviewPostDto;
import com.team33.moduleapi.api.review.mapper.ReviewServiceMapper;
import com.team33.modulecore.core.review.application.ReviewCommandService;
import com.team33.modulecore.core.review.domain.ReviewContext;
import com.team33.modulecore.core.review.domain.entity.Review;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/reviews")
public class ReviewCommandController {

	private final ReviewCommandService reviewCommandService;
	private final ReviewServiceMapper reviewServiceMapper;

	@ResponseStatus(HttpStatus.CREATED)
	@PostMapping
	public SingleResponseDto<ReviewDetailResponseDto> postReview(
		@RequestBody ReviewPostDto reviewPostDto
	) {
		reviewServiceMapper.validate(reviewPostDto);
		ReviewContext reviewContext = reviewServiceMapper.toReviewPostContext(reviewPostDto);

		Review review = reviewCommandService.createReview(reviewContext);

		return new SingleResponseDto<>(ReviewDetailResponseDto.of(review));
	}


	@PatchMapping
	public SingleResponseDto<ReviewDetailResponseDto> updateReview(
		@RequestBody ReviewPatchDto reviewPatchDto
	) {
		ReviewContext reviewContext = reviewServiceMapper.toReviewPatchContext(reviewPatchDto);

		Review updatedReview = reviewCommandService.updateReview(reviewContext);

		return new SingleResponseDto<>(ReviewDetailResponseDto.of(updatedReview));
	}

	@ResponseStatus(HttpStatus.NO_CONTENT)
	@DeleteMapping
	public void deleteReview(
		@RequestBody ReviewDeleteDto reviewDeleteDto
	) {
		ReviewContext reviewContext = reviewServiceMapper.toReviewDeleteContext(reviewDeleteDto);

		reviewCommandService.deleteReview(reviewContext);
	}
}
