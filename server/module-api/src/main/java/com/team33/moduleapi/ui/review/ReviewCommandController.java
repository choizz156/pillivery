package com.team33.moduleapi.ui.review;

import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.team33.moduleapi.dto.SingleResponseDto;
import com.team33.moduleapi.ui.review.dto.ReviewDeleteDto;
import com.team33.moduleapi.ui.review.dto.ReviewDetailResponseDto;
import com.team33.moduleapi.ui.review.dto.ReviewPatchDto;
import com.team33.moduleapi.ui.review.dto.ReviewPostDto;
import com.team33.moduleapi.ui.review.mapper.ReviewServiceMapper;
import com.team33.modulecore.review.application.ReviewCommandService;
import com.team33.modulecore.review.domain.ReviewContext;
import com.team33.modulecore.review.domain.entity.Review;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/reviews")
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
