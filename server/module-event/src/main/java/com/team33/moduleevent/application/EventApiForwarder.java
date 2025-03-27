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
		EventSender suscribeEventSender,
		EventDispatcher eventDispatcher
	) {
		this.eventsRepository = eventsRepository;
		this.kakaoApiEventSender = kakaoApiEventSender;
		this.eventDispatcher = eventDispatcher;
		this.eventTypeEventSenderMap = initializeEventSenders(
			kakaoApiEventSender,
			suscribeEventSender
		);
	}

	@Transactional
	@Scheduled(cron = "0 0 * * * *")
	public void fetchAndForwardEvents() {

		List<ApiEvent> apiEvents = eventsRepository
			.findTop20ByStatusOrderByCreatedAt(EventStatus.READY);

		if (!apiEvents.isEmpty()) {
			apiEvents.forEach(this::registerEventWithDispatcher);
		}
	}

	private void registerEventWithDispatcher(ApiEvent apiEvent) {
		EventType type = apiEvent.getType();
		EventSender eventSender = eventTypeEventSenderMap.getOrDefault(type, kakaoApiEventSender);
		eventDispatcher.register(apiEvent, eventSender);
	}

	private Map<EventType, EventSender> initializeEventSenders(
		EventSender kakaoApiEventSender,
		EventSender suscribeEventSender
	) {
		final Map<EventType, EventSender> eventTypeEventSenderMap = new EnumMap<>(EventType.class);

		eventTypeEventSenderMap.put(EventType.KAKAO_REFUNDED, kakaoApiEventSender);
		eventTypeEventSenderMap.put(EventType.SUBSCRIPTION_CANCELED, kakaoApiEventSender);
		eventTypeEventSenderMap.put(EventType.SUBSCRIPTION_REGISTERED, suscribeEventSender);

		return eventTypeEventSenderMap;
	}
}
