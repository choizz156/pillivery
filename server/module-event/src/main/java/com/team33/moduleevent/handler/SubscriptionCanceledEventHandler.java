package com.team33.moduleevent.handler;

import org.springframework.context.event.EventListener;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;

import com.team33.modulecore.core.payment.kakao.application.events.KakaoSubsCanceledEvent;
import com.team33.moduleevent.domain.EventStatus;
import com.team33.moduleevent.domain.EventType;
import com.team33.moduleevent.domain.entity.ApiEvent;
import com.team33.moduleevent.domain.repository.EventRepository;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Component
public class SubscriptionCanceledEventHandler {

	private final EventRepository eventsRepository;

	@EventListener
	public void onEventSet(KakaoSubsCanceledEvent apiEvent) {

		ApiEvent refund = ApiEvent.builder()
			.contentType(MediaType.APPLICATION_JSON_VALUE)
			.parameters(apiEvent.getCancelParam())
			.url(apiEvent.getCancelUrl())
			.type(EventType.SUBSCRIPTION_CANCELED)
			.status(EventStatus.READY)
			.build();

		eventsRepository.save(refund);
	}
}
