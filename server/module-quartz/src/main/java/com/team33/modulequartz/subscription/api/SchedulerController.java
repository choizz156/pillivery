package com.team33.modulequartz.subscription.api;

import java.time.ZonedDateTime;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.team33.modulequartz.subscription.application.SubscriptionService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api/schedules")
@RestController
public class SchedulerController {

	private final SubscriptionService subscriptionService;

	@ResponseStatus(HttpStatus.ACCEPTED)
	@PostMapping("/{orderId}")
	public void schedule(
		@PathVariable(name = "orderId") long orderId
	) {
		subscriptionService.applySchedule(orderId);
	}

	@ResponseStatus(HttpStatus.NO_CONTENT)
	@PostMapping("/update")
	public void changeTrigger(
		@RequestParam(name = "orderId") long orderId,
		@RequestParam(name = "itemOrderId") long itemOrderId
	) {
		subscriptionService.changeTrigger(orderId,  itemOrderId);
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



