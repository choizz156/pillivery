package com.team33.moduleevent.application;

import org.springframework.stereotype.Component;

import com.team33.moduleevent.domain.entity.ApiEvent;
import com.team33.moduleexternalapi.config.RestTemplateErrorHandler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Component
public class EventProcessor {

	private static final int LIMIT_COUNT = 2;

	private final FailEventService failEventService;

	public void register(ApiEvent apiEvent, EventSender eventSender) {
		int retry = 0;
		boolean isSuccess = false;

		while (check(retry, isSuccess)) {
			try {
				eventSender.send(apiEvent);
				apiEvent.changeStatusToComplete();
				isSuccess = true;
			} catch (Exception e) {
				retry++;
				log.warn("retry : {}, parameters : {}", retry, e.getMessage());
				if (retry == LIMIT_COUNT) {
					apiEvent.changeStatusToFail();
					String reason = RestTemplateErrorHandler.ThreadLocalErrorMessage.get();

					failEventService.saveFailEvent(apiEvent, reason);

					RestTemplateErrorHandler.ThreadLocalErrorMessage.clear();
				}
			}
		}
	}

	private boolean check(int retry, boolean isSuccess) {
		return retry < LIMIT_COUNT && !isSuccess;
	}
}
