package com.team33.moduleapi.ui.review.mapper;

import org.springframework.stereotype.Component;

import com.team33.moduleapi.ui.review.dto.ReviewDeleteDto;
import com.team33.moduleapi.ui.review.dto.ReviewPatchDto;
import com.team33.moduleapi.ui.review.dto.ReviewPostDto;
import com.team33.modulecore.order.domain.entity.Order;
import com.team33.modulecore.order.domain.repository.OrderQueryRepository;
import com.team33.modulecore.review.domain.ReviewContext;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Component
public class ReviewServiceMapper {

	private final OrderQueryRepository orderQueryRepository;

	public ReviewContext toReviewPostContext(ReviewPostDto reviewPostDto) {
		return ReviewContext.builder()
			.displayName(reviewPostDto.getDisplayName())
			.content(reviewPostDto.getContent())
			.star(reviewPostDto.getStar())
			.itemId(reviewPostDto.getItemId())
			.userId(reviewPostDto.getUserId())
			.build();
	}

	public ReviewContext toReviewPatchContext(ReviewPatchDto reviewPostDto) {
		return ReviewContext.builder()
			.reviewId(reviewPostDto.getReviewId())
			.content(reviewPostDto.getContent())
			.star(reviewPostDto.getStar())
			.build();
	}

	public void validate(ReviewPostDto reviewPostDto) {
		Order order = orderQueryRepository.findById(reviewPostDto.getOrderId());

		validatePurchase(reviewPostDto, order);
	}

	private void validatePurchase(ReviewPostDto reviewPostDto, Order order) {
		boolean isPurchased = order.getOrderItems()
			.stream()
			.anyMatch(orderItem -> orderItem.containsItem(reviewPostDto.getItemId()));

		if (!isPurchased) {
			throw new IllegalArgumentException("구매한 상품만 리뷰할 수 있습니다.");
		}
	}

	public ReviewContext toReviewDeleteContext(ReviewDeleteDto reviewDeleteDto) {
		return ReviewContext.builder()
			.reviewId(reviewDeleteDto.getReviewId())
			.itemId(reviewDeleteDto.getItemId())
			.userId(reviewDeleteDto.getUserId())
			.build();
	}
}
