package com.team33.moduleevent.application;

import java.time.LocalDateTime;

import org.springframework.context.event.EventListener;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;

import com.team33.modulecore.eventstore.domain.EventStatus;
import com.team33.modulecore.payment.kakao.application.events.KakaoRefundedEvent;
import com.team33.moduleevent.domain.entity.ApiEventSet;
import com.team33.moduleevent.domain.repository.EventRepository;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class RefundEventHandler {

	private final EventRepository eventsRepository;

	@EventListener
	public void onEventSet(KakaoRefundedEvent apiEvent) {
		ApiEventSet apiEventSet = ApiEventSet.builder()
			.contentType(MediaType.APPLICATION_JSON_VALUE)
			.parameters(apiEvent.getRefundParams())
			.url(apiEvent.getRefundUrl())
			.type(apiEvent.getClass().getSimpleName())
			.localDateTime(LocalDateTime.now())
			.status(EventStatus.READY)
			.build();

		eventsRepository.save(apiEventSet);
	}
}
