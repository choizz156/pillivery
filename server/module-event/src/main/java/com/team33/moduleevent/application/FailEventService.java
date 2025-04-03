package com.team33.moduleevent.application;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import com.team33.modulecore.exception.DataSaveException;
import com.team33.moduleevent.domain.entity.ApiEvent;
import com.team33.moduleevent.domain.entity.FailEvent;
import com.team33.moduleevent.domain.repository.FailEventRepository;
import com.team33.moduleredis.domain.annotation.DistributedLock;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class FailEventService {


	private static final Logger LOGGER = LoggerFactory.getLogger("fileLog");

	private final FailEventRepository failEventRepository;

	@Async
	@DistributedLock(key = "'event:saveFailEvent'")
	public void saveFailEvent(ApiEvent apiEvent, String reason) {

		LOGGER.warn("eventId : {}, type : {}, reason : {}", apiEvent.getId(), apiEvent.getType(), reason);

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
			LOGGER.warn(
				"fail event save error: eventId : {}, type : {}", apiEvent.getId(), apiEvent.getType());
			throw new DataSaveException(e.getMessage());
		}
	}
}
