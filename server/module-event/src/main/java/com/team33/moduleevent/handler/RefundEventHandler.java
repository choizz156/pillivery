package com.team33.moduleevent.handler;

import java.time.LocalDateTime;

import org.springframework.context.event.EventListener;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;

import com.team33.modulecore.payment.kakao.application.events.KakaoRefundedEvent;
import com.team33.moduleevent.domain.EventStatus;
import com.team33.moduleevent.domain.EventType;
import com.team33.moduleevent.domain.entity.ApiEvent;
import com.team33.moduleevent.domain.repository.EventRepository;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class RefundEventHandler {

	private final EventRepository eventsRepository;

	@EventListener
	public void onEventSet(KakaoRefundedEvent apiEvent) {

		ApiEvent apiEventSet = ApiEvent.builder()
			.contentType(MediaType.APPLICATION_JSON_VALUE)
			.parameters(apiEvent.getRefundParams())
			.url(apiEvent.getRefundUrl())
			.type(EventType.KAKAO_REFUNDED.name())
			.createdAt(LocalDateTime.now())
			.status(EventStatus.READY)
			.build();

		eventsRepository.save(apiEventSet);
	}
}
