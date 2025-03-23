package com.team33.moduleevent.handler;

import java.time.LocalDateTime;

import org.springframework.context.event.EventListener;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;

import com.team33.modulecore.core.payment.kakao.application.events.KakaoSubsCanceledEvent;
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
			.type(EventType.SUBS_CANCELED)
			.status(EventStatus.READY)
			.build();

		eventsRepository.save(refund);
	}
}
