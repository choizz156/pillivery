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
import com.team33.moduleredis.domain.annotation.DistributedLock;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class EventApiForwarder {

	private final EventRepository eventsRepository;
	private final EventDispatcher eventDispatcher;
	private final Map<EventType, EventSender> eventTypeEventSenderMap;

	public EventApiForwarder(
		EventRepository eventsRepository,
		EventDispatcher eventDispatcher,
		EventSender kakaoCancelEventSender,
		EventSender subscriptionRegisterEventSender
	) {
		this.eventsRepository = eventsRepository;
		this.eventDispatcher = eventDispatcher;
		this.eventTypeEventSenderMap = initializeEventSenders(
			kakaoCancelEventSender,
			subscriptionRegisterEventSender
		);
	}

	
	@Scheduled(cron = "0 0 * * * *")
	public void fetchAndForwardEvents() {
		List<ApiEvent> apiEvents = fetchReadyEvents();
		
		if (!apiEvents.isEmpty()) {
			apiEvents.forEach(this::registerEventWithDispatcher);
		}
	}

	@DistributedLock(key = "'event:fetchReadyEvents'")
	public List<ApiEvent> fetchReadyEvents() {
		return eventsRepository.findTop20ByStatusOrderByCreatedAt(EventStatus.READY);
	}

	private void registerEventWithDispatcher(ApiEvent apiEvent) {
		EventType type = apiEvent.getType();
		EventSender eventSender = eventTypeEventSenderMap.getOrDefault(type, e -> {});
		eventDispatcher.register(apiEvent, eventSender);
	}

	private Map<EventType, EventSender> initializeEventSenders(
		EventSender kakaoCancelEventSender,
		EventSender subscriptionRegisterEventSender
	) {
		final Map<EventType, EventSender> eventTypeEventSenderMap = new EnumMap<>(EventType.class);

		eventTypeEventSenderMap.put(EventType.KAKAO_REFUNDED, kakaoCancelEventSender);
		eventTypeEventSenderMap.put(EventType.SUBSCRIPTION_CANCELED, kakaoCancelEventSender);
		eventTypeEventSenderMap.put(EventType.SUBSCRIPTION_REGISTERED, subscriptionRegisterEventSender);

		return eventTypeEventSenderMap;
	}
}
