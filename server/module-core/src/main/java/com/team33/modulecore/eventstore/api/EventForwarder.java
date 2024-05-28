package com.team33.modulecore.eventstore.api;

import java.util.List;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.team33.modulecore.eventstore.application.FailEventService;
import com.team33.modulecore.eventstore.domain.ApiEventSet;
import com.team33.modulecore.eventstore.domain.EventStatus;
import com.team33.modulecore.eventstore.domain.EventRepository;
import com.team33.moduleexternalapi.config.RestTemplateErrorHandler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Component
public class EventForwarder {

	private static final int LIMIT_RETRY = 2;

	private final EventRepository eventsRepository;
	private final EventSender eventSender;
	private final FailEventService failEventService;

	@Transactional(propagation = Propagation.REQUIRES_NEW)
	@Scheduled(initialDelay = 1000, fixedDelay = 20000)
	public void getAndSend() {
		List<ApiEventSet> apiEventSets = eventsRepository
			.findTop20ByStatusOrderByCreatedAtDesc(EventStatus.READY);

		if (!apiEventSets.isEmpty()) {
			sendEvent(apiEventSets);
		}
	}

	private void sendEvent(List<ApiEventSet> apiEventSets) {
		apiEventSets.forEach(this::sendToKakao);
	}

	private void sendToKakao(ApiEventSet apiEventSet) {
		int attemp = 0;
		boolean isSuccess = false;

		while (check(attemp, isSuccess)) {
			try {
				sendAndChangeStatus(apiEventSet, isSuccess);
			} catch (Exception e) {
				attemp++;
				log.warn("attempt : {}, parameters : {}", attemp, e.getMessage());
				if (attemp == LIMIT_RETRY) {
					apiEventSet.changeStatusToFail();
					String reason = RestTemplateErrorHandler.ThreadLocalErrorMessage.get();
					failEventService.saveFail(apiEventSet, reason);
				}
			}
		}
	}

	private void sendAndChangeStatus(ApiEventSet apiEventSet, boolean isSuccess) {
		eventSender.send(apiEventSet);
		apiEventSet.changeStatusToComplete();
		isSuccess = true;
	}

	private boolean check(int attemp, boolean isSuccess) {
		return attemp < LIMIT_RETRY && !isSuccess;
	}
}
