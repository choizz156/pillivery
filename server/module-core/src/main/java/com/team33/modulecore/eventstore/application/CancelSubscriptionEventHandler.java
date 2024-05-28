package com.team33.modulecore.eventstore.application;

import org.springframework.context.event.EventListener;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;

import com.team33.modulecore.eventstore.domain.ApiEventSet;
import com.team33.modulecore.eventstore.domain.EventRepository;
import com.team33.modulecore.payment.kakao.application.events.KakaoSubsCanceledEvent;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Component
public class CancelSubscriptionEventHandler {

	private final EventRepository eventsRepository;

	@EventListener
	public void onEventSet(KakaoSubsCanceledEvent apiEvent) {

		ApiEventSet refund = ApiEventSet.builder()
			.contentType(MediaType.APPLICATION_JSON_VALUE)
			.parameters(apiEvent.getCancelParam())
			.url(apiEvent.getCancelUrl())
			.type("subscription cancel")
			.build();

		eventsRepository.save(refund);
	}
}
