package com.team33.moduleapi.ui.review.mapper;

import org.springframework.stereotype.Component;

import com.team33.moduleapi.ui.review.dto.ReviewDto;
import com.team33.modulecore.order.domain.entity.Order;
import com.team33.modulecore.order.domain.repository.OrderQueryRepository;
import com.team33.modulecore.review.domain.ReviewContext;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Component
public class ReviewServiceMapper {

	private final OrderQueryRepository orderQueryRepository;

	public ReviewContext toReviewContext(ReviewDto reviewDto) {
		return ReviewContext.builder()
			.reviewId(reviewDto.getReviewId())
			.displayName(reviewDto.getDisplayName())
			.content(reviewDto.getContent())
			.star(reviewDto.getStar())
			.itemId(reviewDto.getItemId())
			.userId(reviewDto.getUserId())
			.build();
	}

	public void validate(ReviewDto reviewDto) {
		Order order = orderQueryRepository.findById(reviewDto.getOrderId());

		validatePurchase(reviewDto, order);
	}

	private void validatePurchase(ReviewDto reviewDto, Order order) {
		boolean isPurchased = order.getOrderItems()
			.stream()
			.anyMatch(orderItem -> orderItem.containsItem(reviewDto.getItemId()));

		if (!isPurchased) {
			throw new IllegalArgumentException("구매한 상품만 리뷰할 수 있습니다.");
		}
	}

}
