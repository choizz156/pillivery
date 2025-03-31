package com.team33.moduleevent.handler;

import java.time.LocalDateTime;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;

import com.team33.modulecore.core.payment.kakao.application.events.KakaoRefundedEvent;
import com.team33.moduleevent.domain.EventStatus;
import com.team33.moduleevent.domain.EventType;
import com.team33.moduleevent.domain.entity.ApiEvent;
import com.team33.moduleevent.domain.repository.EventRepository;
import com.team33.moduleredis.domain.annotation.DistributedLock;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Component
public class RefundEventHandler {

	private static final Logger LOGGER = LoggerFactory.getLogger("fileLog");

	private final EventRepository eventsRepository;

	@DistributedLock(key = "'payment:refunded:' + #apiEvent.refundParams")
	@EventListener
	public void onEventSet(KakaoRefundedEvent apiEvent) {

		eventsRepository.findByTypeAndParameters(EventType.KAKAO_REFUNDED, apiEvent.getRefundParams())
		.ifPresent(event -> {
			LOGGER.info("중복 이벤트 발생 = {}", apiEvent.getRefundParams());
			return;
		});


		ApiEvent apiEventSet = ApiEvent.builder()
			.contentType(MediaType.APPLICATION_JSON_VALUE)
			.parameters(apiEvent.getRefundParams())
			.url(apiEvent.getRefundUrl())
			.type(EventType.KAKAO_REFUNDED)
			.createdAt(LocalDateTime.now())
			.status(EventStatus.READY)
			.build();

		eventsRepository.save(apiEventSet);
	}
}
