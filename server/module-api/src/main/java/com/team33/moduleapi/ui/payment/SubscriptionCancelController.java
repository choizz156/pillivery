package com.team33.moduleapi.ui.payment;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.team33.moduleapi.dto.SingleResponseDto;
import com.team33.modulecore.order.application.OrderStatusService;
import com.team33.modulecore.payment.application.cancel.CancelSubscriptionService;
import com.team33.moduleexternalapi.dto.KakaoSubsCancelResponse;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@RestController
@RequestMapping("/payments/subscriptions/cancel")
public class SubscriptionCancelController {

	private final CancelSubscriptionService<KakaoSubsCancelResponse> kakaoSubsCancelService;
	private final OrderStatusService orderStatusService;

	@ResponseStatus(HttpStatus.CREATED)
	@PostMapping("/{orderId}")
	public SingleResponseDto<?> refundSubscription(
		@PathVariable Long orderId
	) {
		orderStatusService.processSubscriptionCancel(orderId);

		return new SingleResponseDto<>(kakaoSubsCancelService.cancelSubscription(orderId));
	}
}
