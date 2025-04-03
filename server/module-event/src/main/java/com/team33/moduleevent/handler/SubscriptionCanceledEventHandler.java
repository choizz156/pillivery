package com.team33.moduleevent.handler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;

import com.team33.modulecore.core.payment.kakao.application.events.KakaoSubsCanceledEvent;
import com.team33.moduleevent.domain.EventStatus;
import com.team33.moduleevent.domain.EventType;
import com.team33.moduleevent.domain.entity.ApiEvent;
import com.team33.moduleevent.domain.repository.EventRepository;
import com.team33.moduleredis.domain.annotation.DistributedLock;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Component
public class SubscriptionCanceledEventHandler {

	private static final Logger LOGGER = LoggerFactory.getLogger("fileLog");

	private final EventRepository eventsRepository;

	@EventListener
	@DistributedLock(key = "'subscription:canceled:' + #apiEvent.cancelParam")
	public void onEventSet(KakaoSubsCanceledEvent apiEvent) {

		if (isPresentDuplicatedEvent(apiEvent)) {
			LOGGER.info("중복 이벤트 발생 = {}", apiEvent.getCancelParam());
			return;
		}

		ApiEvent refund = ApiEvent.builder()
			.contentType(MediaType.APPLICATION_JSON_VALUE)
			.parameters(apiEvent.getCancelParam())
			.url(apiEvent.getCancelUrl())
			.type(EventType.SUBSCRIPTION_CANCELED)
			.status(EventStatus.READY)
			.build();

		eventsRepository.save(refund);
	}

	private boolean isPresentDuplicatedEvent(KakaoSubsCanceledEvent apiEvent) {

		return eventsRepository
			.findByTypeAndParameters(EventType.SUBSCRIPTION_CANCELED, apiEvent.getCancelParam())
			.isPresent();
	}
}
