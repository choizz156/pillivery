package com.team33.modulecore.eventstore.api;

import java.util.List;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.team33.modulecore.eventstore.domain.ApiEventSet;
import com.team33.modulecore.eventstore.domain.EventStatus;
import com.team33.modulecore.eventstore.domain.JpaEventsRepository;
import com.team33.moduleexternalapi.exception.PaymentApiException;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Component
public class RefundEventForwarder {

	private final JpaEventsRepository jpaEventsRepository;
	private final EventSender eventSender;

	@Transactional
	@Scheduled(initialDelay = 1000, fixedDelay = 20000)
	public void getAndSend() {
		List<ApiEventSet> apiEventSets = jpaEventsRepository
			.findTop20ByStatusOrderByCreatedAtDesc(EventStatus.READY);

		if (!apiEventSets.isEmpty()) {
			sendEvent(apiEventSets);
		}
	}

	private void sendEvent(List<ApiEventSet> apiEventSets) {
		try {
			for (ApiEventSet apiEventSet : apiEventSets) {
				eventSender.send(apiEventSet);
				apiEventSet.changeStatusToComplete();
			}
		} catch (PaymentApiException e) {
			log.error("Failed to send events {}", e.getMessage());
		}
	}
}
