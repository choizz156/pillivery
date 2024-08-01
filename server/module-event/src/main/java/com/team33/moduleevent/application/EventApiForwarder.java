package com.team33.moduleevent.application;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.team33.moduleevent.domain.EventStatus;
import com.team33.moduleevent.domain.EventType;
import com.team33.moduleevent.domain.entity.ApiEvent;
import com.team33.moduleevent.domain.repository.EventRepository;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class EventApiForwarder {

	private final EventRepository eventsRepository;
	private final EventSender kakaoApiEventSender;
	private final EventDispatcher eventDispatcher;
	private final Map<EventType, EventSender> eventTypeEventSenderMap;

	public EventApiForwarder(
		EventRepository eventsRepository,
		EventSender kakaoApiEventSender,
		EventSender scheduleRegisterEventSender,
		EventSender scheduleCancelEventSender,
		EventDispatcher eventDispatcher
	) {
		this.eventsRepository = eventsRepository;
		this.kakaoApiEventSender = kakaoApiEventSender;
		this.eventDispatcher = eventDispatcher;

		eventTypeEventSenderMap = new EnumMap<>(EventType.class);
		eventTypeEventSenderMap.put(EventType.KAKAO_REFUNDED, kakaoApiEventSender);
		eventTypeEventSenderMap.put(EventType.SCHEDULE_REGISTERED, scheduleRegisterEventSender);
		eventTypeEventSenderMap.put(EventType.SCHEDULE_CANCELED, scheduleCancelEventSender);
		eventTypeEventSenderMap.put(EventType.SUBS_CANCELED,kakaoApiEventSender);

	}

	@Transactional
	@Scheduled(cron = "0 * * * * *")
	public void getAndSend() {
		List<ApiEvent> apiEvents = eventsRepository
			.findTop20ByStatusOrderByCreatedAt(EventStatus.READY);

		if (!apiEvents.isEmpty()) {
			sendEvents(apiEvents);
		}
	}

	private void sendEvents(List<ApiEvent> apiEvents) {
		for (ApiEvent apiEvent : apiEvents) {
			registerEventWithDispatcher(apiEvent);
		}
	}

	private void registerEventWithDispatcher(ApiEvent apiEvent) {
		EventType type = apiEvent.getType();
		EventSender eventSender = eventTypeEventSenderMap.getOrDefault(type, kakaoApiEventSender);
		eventDispatcher.register(apiEvent, eventSender);
	}
}
