package com.team33.moduleevent.application;

import org.springframework.context.event.EventListener;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;

import com.team33.modulecore.payment.kakao.application.events.KakaoSubsCanceledEvent;
import com.team33.moduleevent.domain.EventStatus;
import com.team33.moduleevent.domain.EventType;
import com.team33.moduleevent.domain.entity.ApiEvent;
import com.team33.moduleevent.domain.repository.EventRepository;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class SubsCanceledEventHandler {

	private final EventRepository eventsRepository;

	@EventListener
	public void onEventSet(KakaoSubsCanceledEvent apiEvent) {

		ApiEvent refund = ApiEvent.builder()
			.contentType(MediaType.APPLICATION_JSON_VALUE)
			.parameters(apiEvent.getCancelParam())
			.url(apiEvent.getCancelUrl())
			.type(EventType.SUBS_CANCELED.name())
			.status(EventStatus.READY)
			.build();

		eventsRepository.save(refund);
	}
}
