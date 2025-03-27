package com.team33.moduleevent.handler;

import java.time.LocalDateTime;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;
import org.springframework.dao.DataAccessException;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import com.team33.modulecore.core.common.OrderFindHelper;
import com.team33.modulecore.core.order.domain.entity.Order;
import com.team33.modulecore.core.payment.application.SubscriptionOrderService;
import com.team33.modulecore.core.payment.domain.entity.SubscriptionOrder;
import com.team33.modulecore.core.payment.kakao.application.events.SubscriptionRegisteredEvent;
import com.team33.modulecore.exception.DataSaveException;
import com.team33.moduleevent.domain.EventStatus;
import com.team33.moduleevent.domain.EventType;
import com.team33.moduleevent.domain.entity.ApiEvent;
import com.team33.moduleevent.domain.repository.EventRepository;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Component
public class SubscriptionRegisterHandler {

	private final static Logger LOGGER = LoggerFactory.getLogger("fileLog");
	public static final String URL = "https://localhost:8080/api/payments/subscriptions/";

	private final SubscriptionOrderService subscriptionOrderService;
	private final EventRepository eventRepository;
	private final OrderFindHelper orderFindHelper;

	@Async
	@EventListener
	public void onEventSet(SubscriptionRegisteredEvent event) {
		Order order = orderFindHelper.findOrder(event.getOrderId());
		List<SubscriptionOrder> subscriptionOrders = subscriptionOrderService.create(order);

		subscriptionOrders.forEach(subscriptionOrder -> {
			ApiEvent apiEvent = toEvent(subscriptionOrder.getId());
			saveEvent(subscriptionOrder.getId(), apiEvent);
		});

	}

	private ApiEvent toEvent(Long subscriptionOrderId) {
		return ApiEvent.builder()
			.contentType("String")
			.parameters(String.valueOf(subscriptionOrderId))
			.url(URL)
			.type(EventType.SUBSCRIPTION_REGISTERED)
			.createdAt(LocalDateTime.now())
			.status(EventStatus.READY)
			.build();
	}

	private void saveEvent(Long event, ApiEvent apiEvent) {
		try {
			eventRepository.save(apiEvent);
		} catch (DataAccessException e) {
			LOGGER.warn("정기 구독 승인 이벤트 저장 실패 :: orderId={}, message = {}", event, e.getMessage());
			throw new DataSaveException(e.getMessage(), e.getCause());
		}
	}

}
