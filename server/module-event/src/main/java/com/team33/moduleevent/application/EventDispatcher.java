package com.team33.moduleevent.application;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.team33.moduleevent.domain.entity.ApiEvent;
import com.team33.moduleexternalapi.config.RestTemplateErrorHandler;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Component
public class EventDispatcher {

	private static final Logger log = LoggerFactory.getLogger("fileLog");
	private static final int MAX_RETRIES = 2;

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
				handleFailure(apiEvent, retry);
			}
		}
	}

	private void sendEvent(ApiEvent apiEvent, EventSender eventSender) {
		eventSender.send(apiEvent);
		apiEvent.changeStatusToComplete();
	}

	private void handleFailure(ApiEvent apiEvent, int retry) {
		if (retry == MAX_RETRIES) {
			apiEvent.changeStatusToFail();

			String reason = RestTemplateErrorHandler.ThreadLocalErrorMessage.get();
			RestTemplateErrorHandler.ThreadLocalErrorMessage.clear();

			failEventService.saveFailEvent(apiEvent, reason);
		}
	}

	private boolean check(int retry, boolean isSuccess) {
		return retry < MAX_RETRIES && !isSuccess;
	}
}
