package com.team33.moduleevent.handler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;
import org.springframework.dao.DataAccessException;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import com.team33.modulecore.core.order.application.OrderItemService;
import com.team33.modulecore.exception.DataSaveException;
import com.team33.modulequartz.subscription.domain.PaymentDateUpdatedEvent;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class PaymentDateUpdatedHandler {

	private final static Logger log = LoggerFactory.getLogger("fileLog");

	private final OrderItemService orderItemService;

	@Async
	@EventListener
	public void onEventSet(PaymentDateUpdatedEvent apiEvent) {
		updatePaymentDate(apiEvent);
	}

	private void updatePaymentDate(PaymentDateUpdatedEvent apiEvent) {
		try {
			orderItemService.updateNextPaymentDate(apiEvent.getPaymentDay(), apiEvent.getOrderItemId());
		} catch (DataAccessException e) {
			log.error("다음 결제일 저장 에러 = {}, id = {}, 주기 = {}", e.getMessage(),
				apiEvent.getOrderItemId()
			);
			throw new DataSaveException(e.getMessage());
		}
	}
}
