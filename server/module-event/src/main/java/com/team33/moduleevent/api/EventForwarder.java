package com.team33.moduleevent.api;

import java.util.List;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.team33.modulecore.eventstore.domain.EventStatus;
import com.team33.moduleevent.application.FailEventService;
import com.team33.moduleevent.domain.entity.ApiEventSet;
import com.team33.moduleevent.domain.repository.EventRepository;
import com.team33.moduleexternalapi.config.RestTemplateErrorHandler.ThreadLocalErrorMessage;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Component
public class EventForwarder {

	private static final int LIMIT_COUNT = 2;

	private final EventRepository eventsRepository;
	private final EventSender eventSender;
	private final FailEventService failEventService;

	@Transactional(propagation = Propagation.REQUIRES_NEW)
	// @Scheduled(initialDelay = 1000, fixedDelay = 20000)
	public void getAndSend() {
		List<ApiEventSet> apiEventSets = eventsRepository
			.findTop20ByStatusOrderByCreatedAt(EventStatus.READY);

		if (!apiEventSets.isEmpty()) {
			sendEvent(apiEventSets);
		}
	}

	private void sendEvent(List<ApiEventSet> apiEventSets) {
		apiEventSets.forEach(this::sendToKakao);
	}

	private void sendToKakao(ApiEventSet apiEventSet) {
		int retry = 0;
		boolean isSuccess = false;

		while (check(retry, isSuccess)) {
			try {
				sendAndChangeStatus(apiEventSet);
				isSuccess = true;
			} catch (Exception e) {
				retry++;
				log.warn("retry : {}, parameters : {}", retry, e.getMessage());
				if (retry == LIMIT_COUNT) {
					apiEventSet.changeStatusToFail();
					String reason = ThreadLocalErrorMessage.get();
					log.error("eventId : {}, type : {}, reason : {}", apiEventSet.getId(), apiEventSet.getType(), reason);
					failEventService.saveFail(apiEventSet, reason);
					ThreadLocalErrorMessage.clear();
				}
			}
		}
	}

	private void sendAndChangeStatus(ApiEventSet apiEventSet) {
		eventSender.send(apiEventSet);
		apiEventSet.changeStatusToComplete();
	}

	private boolean check(int retry, boolean isSuccess) {
		return retry < LIMIT_COUNT && !isSuccess;
	}
}
