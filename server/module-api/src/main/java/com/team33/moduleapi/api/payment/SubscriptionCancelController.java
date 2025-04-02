package com.team33.moduleapi.api.payment;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.team33.moduleapi.response.SingleResponseDto;
import com.team33.modulecore.core.order.application.OrderStatusService;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/payments/subscriptions/cancel")
public class SubscriptionCancelController {

	private final OrderStatusService orderStatusService;

	@ResponseStatus(HttpStatus.CREATED)
	@PostMapping("/{subscriptionOrderId}")
	public SingleResponseDto<String> refundSubscription(
		@PathVariable Long subscriptionOrderId
	) {
		orderStatusService.processSubscriptionCancel(subscriptionOrderId);
		return new SingleResponseDto<>("complete");
	}
}
