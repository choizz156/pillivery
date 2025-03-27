package com.team33.moduleapi.api.scheduler;

import java.time.ZonedDateTime;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.team33.modulecore.core.order.application.OrderItemService;
import com.team33.modulecore.core.payment.application.SubscriptionOrderService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api/subscriptions")
@RestController
public class SubscriptionController {

	private final SubscriptionOrderService subscriptionOrderService;

	@ResponseStatus(HttpStatus.NO_CONTENT)
	@PatchMapping
	public void changePeriod(
		@RequestParam(name = "period") int period,
		@RequestParam(name = "itemOrderId") long itemOrderId
	) {
		subscriptionOrderService.changeItemPeriod(period, itemOrderId);
	}

	@DeleteMapping
	public ZonedDateTime delete(
		@RequestParam(name = "itemOrderId") Long itemOrderId
	) {
		subscriptionOrderService.cancelSubscription(itemOrderId);
		return ZonedDateTime.now();
	}

}



