package com.team33.moduleevent.application;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.team33.moduleevent.domain.entity.ApiEvent;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Component
public class EventDispatcher {

	private static final Logger LOGGER = LoggerFactory.getLogger("fileLog");
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
				String message = e.getMessage();
				LOGGER.warn("retry : {}, parameters : {}", retry, message);
				handleFailure(apiEvent, retry, message);
			}
		}
	}

	private void sendEvent(ApiEvent apiEvent, EventSender eventSender) {
		eventSender.send(apiEvent);
		apiEvent.changeStatusToComplete();
	}

	private void handleFailure(ApiEvent apiEvent, int retry, String message) {
		if (retry == MAX_RETRIES) {
			apiEvent.changeStatusToFail();

			failEventService.saveFailEvent(apiEvent, message);
		}
	}

	private boolean check(int retry, boolean isSuccess) {
		return retry < MAX_RETRIES && !isSuccess;
	}
}
