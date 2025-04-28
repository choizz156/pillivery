package com.team33.modulecore.core.payment.kakao.application.events.handler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;
import org.springframework.dao.DataAccessException;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import com.team33.modulecore.core.order.application.SubscriptionOrderService;
import com.team33.modulecore.core.payment.kakao.application.events.SubscriptionFailedEvent;
import com.team33.moduleredis.domain.annotation.DistributedLock;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Component
public class SubscriptionFailedHandler {

	private final static Logger LOGGER = LoggerFactory.getLogger("fileLog");

	private final SubscriptionOrderService subscriptionOrderService;

	@Async
	@DistributedLock(key = "'subscription:failed:' + #apiEvent.subscriptionOrderId")
	@EventListener
	public void onEventSet(SubscriptionFailedEvent apiEvent) {
		failSubscriptionOrderStatus(apiEvent);
	}

	private void failSubscriptionOrderStatus(SubscriptionFailedEvent apiEvent) {

		try {
			subscriptionOrderService.updateOrderStatusToFail(apiEvent.getSubscriptionOrderId());
		} catch (DataAccessException e) {
			LOGGER.info( "정기 결제 상태 업데이트 실패( 정기 결제 실패) = {}, id = {}, 결제일 = {}",
				e.getMessage(),
				apiEvent.getSubscriptionOrderId()
			);
		}
	}
}
