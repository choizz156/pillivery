package com.team33.moduleapi.ui.schedule;

import java.time.ZonedDateTime;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.team33.moduleapi.dto.SingleResponseDto;
import com.team33.moduleapi.ui.order.dto.OrderItemSimpleResponse;
import com.team33.modulecore.core.order.domain.entity.OrderItem;
import com.team33.modulequartz.subscription.application.SubscriptionService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@RequestMapping("/schedules")
@RestController
public class ScheduleController {

	private final SubscriptionService subscriptionService;

	@ResponseStatus(HttpStatus.ACCEPTED)
	@PostMapping("/{orderId}")
	public void schedule(
		@PathVariable(name = "orderId") long orderId
	) {
		subscriptionService.applySchedule(orderId);
	}

	@PatchMapping
	public SingleResponseDto<OrderItemSimpleResponse> changePeriod(
		@RequestParam(name = "orderId") long orderId,
		@RequestParam(name = "period") int period,
		@RequestParam(name = "itemOrderId") long itemOrderId
	) {
		OrderItem orderItem = subscriptionService.changePeriod(orderId, period, itemOrderId);

		return new SingleResponseDto<>(
			OrderItemSimpleResponse.of(orderItem)
		);
	}

	@DeleteMapping
	public ZonedDateTime delete(
		@RequestParam(name = "orderId") Long orderId,
		@RequestParam(name = "itemOrderId") Long itemOrderId
	) {
		subscriptionService.cancelScheduler(orderId, itemOrderId);
		return ZonedDateTime.now();
	}
}



