package com.team33.moduleapi.ui.payment;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.team33.moduleapi.dto.SingleResponseDto;
import com.team33.modulecore.core.order.application.OrderStatusService;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@RestController
@RequestMapping("/payments/subscriptions/cancel")
public class SubscriptionCancelController {

	private final OrderStatusService orderStatusService;

	@ResponseStatus(HttpStatus.CREATED)
	@PostMapping("/{orderId}")
	public SingleResponseDto<String> refundSubscription(
		@PathVariable long orderId
	) {
		orderStatusService.processSubscriptionCancel(orderId);
		return new SingleResponseDto<>("complete");
	}
}
