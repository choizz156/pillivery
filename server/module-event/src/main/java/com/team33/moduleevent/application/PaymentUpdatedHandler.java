package com.team33.moduleevent.application;

import java.time.ZoneId;
import java.time.ZonedDateTime;

import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import com.team33.modulecore.order.application.OrderItemService;
import com.team33.modulequartz.subscription.domain.PaymentUpdatedEvent;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class PaymentUpdatedHandler {

	private final OrderItemService orderItemService;


	@Async
	@EventListener
	public void onEventSet(PaymentUpdatedEvent apiEvent) {
		ZonedDateTime paymentDay = ZonedDateTime.now(ZoneId.of("Asia/Seoul"));
		orderItemService.updateNextPaymentDate(paymentDay, apiEvent.getOrderItem());
	}
}
