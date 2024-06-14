package com.team33.moduleevent.application;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.team33.moduleevent.domain.entity.ApiEvent;
import com.team33.moduleexternalapi.config.RestTemplateErrorHandler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Component
public class EventDispatcher {

	private static final int LIMIT_COUNT = 2;

	private final FailEventService failEventService;

	@Transactional
	public void register(ApiEvent apiEvent, EventSender eventSender) {
		int retry = 0;
		boolean isSuccess = false;

		while (check(retry, isSuccess)) {
			try {
				sendEvent(apiEvent, eventSender);
				isSuccess = true;
			} catch (RuntimeException e) {
				retry++;
				log.warn("retry : {}, parameters : {}", retry, e.getMessage());
				saveFailEvent(apiEvent, retry);
			}
		}
	}

	private void sendEvent(ApiEvent apiEvent, EventSender eventSender) {
		eventSender.send(apiEvent);
		apiEvent.changeStatusToComplete();
	}

	private void saveFailEvent(ApiEvent apiEvent, int retry) {
		if (retry == LIMIT_COUNT) {
			apiEvent.changeStatusToFail();

			String reason = RestTemplateErrorHandler.ThreadLocalErrorMessage.get();
			RestTemplateErrorHandler.ThreadLocalErrorMessage.clear();

			failEventService.saveFailEvent(apiEvent, reason);
		}
	}

	private boolean check(int retry, boolean isSuccess) {
		return retry < LIMIT_COUNT && !isSuccess;
	}
}
