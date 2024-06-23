package com.team33.moduleapi.ui.schedule;

import java.time.ZonedDateTime;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.team33.modulecore.common.OrderFindHelper;
import com.team33.modulecore.order.application.OrderItemService;
import com.team33.modulequartz.subscription.application.SubscriptionService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@RequestMapping("/schedules")
@RestController
public class ScheduleController {

	private final SubscriptionService subscriptionService;
	private final OrderFindHelper orderFindHelper;
	private final OrderItemService orderItemService;

	@ResponseStatus(HttpStatus.ACCEPTED)
	@PostMapping("/{orderId}")
	public void schedule(
		@PathVariable(name = "orderId") long orderId
	) {
		subscriptionService.applySchedule(orderId);
	}

	// @PatchMapping
	// public SingleResponseDto<SubResponse> changePeriod(
	// 	@RequestParam(name = "orderId") Long orderId,
	// 	@RequestParam(name = "period") Integer period,
	// 	@RequestParam(name = "itemOrderId") Long itemOrderId
	// ) {
	// 	log.info("스케쥴 변화");
	// 	OrderItem orderItem = subscriptionService.changePeriod(orderId, period, itemOrderId);
	// 	return new SingleResponseDto<>(
	// 		itemOrderMapper.itemOrderToSubResponse(orderItem, itemMapper)
	// 	);
	// }

	@ResponseStatus(HttpStatus.NO_CONTENT)
	@DeleteMapping
	public ZonedDateTime delete(
		@RequestParam(name = "orderId") Long orderId,
		@RequestParam(name = "itemOrderId") Long itemOrderId
	) {
		subscriptionService.cancelScheduler(orderId, itemOrderId);
		return ZonedDateTime.now();
	}
}



