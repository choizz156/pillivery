package com.team33.modulecore.eventstore.application;

import org.springframework.context.event.EventListener;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;

import com.team33.modulecore.eventstore.domain.entity.ApiEventSet;
import com.team33.modulecore.eventstore.domain.repository.EventRepository;
import com.team33.modulecore.payment.kakao.application.events.KakaoSubsCanceledEvent;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class CancelSubscriptionEventHandler {

	private final EventRepository eventsRepository;

	@EventListener
	public void onEventSet(KakaoSubsCanceledEvent apiEvent) {

		ApiEventSet refund = ApiEventSet.builder()
			.contentType(MediaType.APPLICATION_JSON_VALUE)
			.parameters(apiEvent.getCancelParam())
			.url(apiEvent.getCancelUrl())
			.type(apiEvent.getClass().getSimpleName())
			.build();

		eventsRepository.save(refund);
	}
}
