package com.team33.moduleevent.application;

import org.springframework.dao.DataAccessException;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import com.team33.modulecore.exception.DataSaveException;
import com.team33.moduleevent.domain.entity.ApiEvent;
import com.team33.moduleevent.domain.entity.FailEvent;
import com.team33.moduleevent.domain.repository.FailEventRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Service
public class FailEventService {

	private final FailEventRepository failEventRepository;

	@Async
	public void saveFailEvent(ApiEvent apiEvent, String reason) {
		log.error("eventId : {}, type : {}, reason : {}", apiEvent.getId(), apiEvent.getType(), reason);

		FailEvent failEvent = FailEvent.builder()
			.type(apiEvent.getType())
			.payload(apiEvent.getParameters())
			.reason(reason)
			.build();

		saveFailEvent(apiEvent, failEvent);
	}

	private void saveFailEvent(ApiEvent apiEvent, FailEvent failEvent) {
		try {
			failEventRepository.save(failEvent);
		} catch (DataAccessException e) {
			log.error(
				"fail event save error: eventId : {}, type : {}", apiEvent.getId(), apiEvent.getType());
			throw new DataSaveException(e.getMessage());
		}
	}
}
