package com.team33.modulecore.review.application;

import org.springframework.stereotype.Service;

import com.team33.modulecore.common.OrderFindHelper;
import com.team33.modulecore.exception.BusinessLogicException;
import com.team33.modulecore.exception.ExceptionCode;
import com.team33.modulecore.order.domain.OrderStatus;
import com.team33.modulecore.order.domain.entity.Order;
import com.team33.modulecore.review.domain.ReviewContext;
import com.team33.modulecore.review.domain.repository.ReviewCommandRepository;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class ReviewValidator {

	private final ReviewCommandRepository reviewCommandRepository;
	private final OrderFindHelper orderFindHelper;

	public void validate(ReviewContext reviewContext) {
		Order order = orderFindHelper.findOrder(reviewContext.getOrderId());

		validateOrderStatus(order.getOrderStatus());
		validatePurchase(reviewContext, order);
	}

	private void validatePurchase(ReviewContext reviewPostDto, Order order) {
		order.getOrderItems()
			.stream()
			.filter(orderItem -> orderItem.containsItem(reviewPostDto.getItemId()))
			.findFirst()
			.orElseThrow(() -> new IllegalArgumentException("구매한 상품만 리뷰할 수 있습니다."));
	}

	private void validateOrderStatus(OrderStatus orderStatus) {
		if (orderStatus == OrderStatus.Refund || orderStatus == OrderStatus.REQUEST) {
			throw new IllegalArgumentException("구매한 상품만 리뷰할 수 있습니다.");
		}
	}

	private void checkDuplicateReview(ReviewContext context) {
		boolean duplicated = reviewCommandRepository.findDuplicated(context.getItemId(), context.getUserId());
		if (duplicated) {
			throw new BusinessLogicException(ExceptionCode.DUPLICATED_REVIEW);
		}

	}
}