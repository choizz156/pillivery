package com.team33.modulecore.core.payment.kakao.application.events.handler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;
import org.springframework.dao.DataAccessException;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import com.team33.modulecore.core.order.application.SubscriptionOrderService;
import com.team33.modulecore.core.payment.kakao.application.events.PaymentDateUpdatedEvent;
import com.team33.moduleredis.domain.annotation.DistributedLock;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Component
public class PaymentDateUpdatedHandler {

	private final static Logger LOGGER = LoggerFactory.getLogger("fileLog");

	private final SubscriptionOrderService subscriptionOrderService;

	@Async
	@DistributedLock(key = "'payment:date:updated:' + #apiEvent.subscriptionOrderId")
	@EventListener
	public void onEventSet(PaymentDateUpdatedEvent apiEvent) {
		updatePaymentDate(apiEvent);
	}

	private void updatePaymentDate(PaymentDateUpdatedEvent apiEvent) {

		try {
			subscriptionOrderService.updateNextPaymentDate(apiEvent.getPaymentDay(), apiEvent.getSubscriptionOrderId());
		} catch (DataAccessException e) {
			LOGGER.error("다음 결제일 저장 에러 = {}, id = {}, 결제일 = {}", e.getMessage(),
				apiEvent.getSubscriptionOrderId(), apiEvent.getPaymentDay()
			);
		}
	}
}
