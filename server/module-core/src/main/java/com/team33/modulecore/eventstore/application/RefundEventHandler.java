package com.team33.modulecore.eventstore.application;

import java.time.LocalDateTime;

import org.springframework.context.event.EventListener;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;

import com.team33.modulecore.eventstore.domain.ApiEventSet;
import com.team33.modulecore.eventstore.domain.JpaEventsRepository;
import com.team33.modulecore.payment.kakao.application.events.KakaoRefundedEvent;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Component
public class RefundEventHandler {

	private final JpaEventsRepository jpaEventsRepository;

	@EventListener(KakaoRefundedEvent.class)
	public void onEventSet(KakaoRefundedEvent apiEvent) {

		ApiEventSet refund = ApiEventSet.builder()
			.contentType(MediaType.APPLICATION_JSON_VALUE)
			.parameters(apiEvent.getParams())
			.url(apiEvent.getUrl())
			.type("refund")
			.createdAt(LocalDateTime.now())
			.build();

		jpaEventsRepository.save(refund);
	}
}
