package com.team33.moduleapi.ui.review.mapper;

import org.springframework.stereotype.Component;

import com.team33.moduleapi.ui.review.dto.ReviewDeleteDto;
import com.team33.moduleapi.ui.review.dto.ReviewPatchDto;
import com.team33.moduleapi.ui.review.dto.ReviewPostDto;
import com.team33.modulecore.core.common.OrderFindHelper;
import com.team33.modulecore.core.order.domain.OrderStatus;
import com.team33.modulecore.core.order.domain.entity.Order;
import com.team33.modulecore.core.review.domain.ReviewContext;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Component
public class ReviewServiceMapper {

	private final OrderFindHelper orderFindHelper;

	public ReviewContext toReviewPostContext(ReviewPostDto reviewPostDto) {
		return ReviewContext.builder()
			.displayName(reviewPostDto.getDisplayName())
			.orderId(reviewPostDto.getOrderId())
			.content(reviewPostDto.getContent())
			.star(reviewPostDto.getStar())
			.itemId(reviewPostDto.getItemId())
			.userId(reviewPostDto.getUserId())
			.build();
	}

	public ReviewContext toReviewPatchContext(ReviewPatchDto reviewPatchDto) {
		return ReviewContext.builder()
			.userId(reviewPatchDto.getUserId())
			.reviewId(reviewPatchDto.getReviewId())
			.content(reviewPatchDto.getContent())
			.star(reviewPatchDto.getStar())
			.build();
	}

	public ReviewContext toReviewDeleteContext(ReviewDeleteDto reviewDeleteDto) {
		return ReviewContext.builder()
			.reviewId(reviewDeleteDto.getReviewId())
			.itemId(reviewDeleteDto.getItemId())
			.userId(reviewDeleteDto.getUserId())
			.build();
	}

	public void validate(ReviewPostDto reviewPostDto) {
		Order order = orderFindHelper.findOrder(reviewPostDto.getOrderId());

		validateOrderStatus(order.getOrderStatus());
		validatePurchase(reviewPostDto, order);
	}

	private void validatePurchase(ReviewPostDto reviewPostDto, Order order) {
		order.getOrderItems()
			.stream()
			.filter(orderItem -> orderItem.containsItem(reviewPostDto.getItemId()))
			.findFirst()
			.orElseThrow(() -> new IllegalArgumentException("구매한 상품만 리뷰할 수 있습니다."));
	}

	private void validateOrderStatus(OrderStatus orderStatus) {
		if (orderStatus == OrderStatus.REFUND || orderStatus == OrderStatus.REQUEST) {
			throw new IllegalArgumentException("구매한 상품만 리뷰할 수 있습니다.");
		}
	}

}
