package com.team33.modulecore.eventstore;

import org.springframework.context.event.EventListener;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;

import com.team33.modulecore.payment.kakao.application.events.KakaoRefundedEvent;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Component
public class RefundEventHandler {

	private final EventsRepository jpaRefundEventRepository;

	@EventListener(KakaoRefundedEvent.class)
	public void onEventSet(KakaoRefundedEvent apiEvent) {

		ApiEventSet.builder()
			.contentType(MediaType.APPLICATION_JSON_VALUE)
			.parameters(apiEvent.getParams())
			.url(apiEvent.getUrl())
			.type("refund")
			.build();

		jpaRefundEventRepository.save(apiEvent);
	}
}
