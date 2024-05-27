package com.team33.modulecore.eventstore;

import java.util.List;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Component
public class RefundEventForwarder {

	private static final int DEFAULT_LIMIT_SIZE = 20;

	private final EventsRepository eventsRepository;
	private final OffsetRepository offsetRepository;
	private final EventSender eventSender;

	@Scheduled(initialDelay = 1000, fixedDelay = 1000)
	public void getAndSend() {
		long nextOffset = offsetRepository.findTopByOrderByCreatedAtDesc().getNextOffset();
		List<ApiEventSet> apiEventSets = eventsRepository.get(nextOffset, DEFAULT_LIMIT_SIZE);

		if (!apiEventSets.isEmpty()) {
			int processedSize = sendEvent(apiEventSets);
			if (processedSize > 0) {
				offsetRepository.update(nextOffset + processedSize);
			}
		}
	}

	private int sendEvent(List<ApiEventSet> apiEventSets) {
		int processedSize = 0;

		try {
			for (ApiEventSet apiEventSet : apiEventSets) {
				eventSender.send(apiEventSet);
				processedSize++;
			}
		} catch (Exception e) {
			log.error("Failed to send events {}", e.getMessage());
		}
		return processedSize;
	}
}
