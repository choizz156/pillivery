package com.team33.modulecore.eventstore.application;

import java.util.List;

import org.springframework.stereotype.Service;

import com.team33.modulecore.eventstore.domain.ApiEventSet;
import com.team33.modulecore.eventstore.domain.EventStatus;
import com.team33.modulecore.eventstore.domain.FailEvent;
import com.team33.modulecore.eventstore.domain.FailEventRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Service
public class FailEventService {

	private final FailEventRepository failEventRepository;

	public void saveFail(ApiEventSet apiEventSet, String reason) {
		log.error("eventId : {}, type : {}, reason : {}", apiEventSet.getId(), apiEventSet.getType(), reason);

		FailEvent failEvent = FailEvent.builder()
			.type(apiEventSet.getType())
			.payload(apiEventSet.getParameters())
			.reason(reason)
			.build();

		failEventRepository.save(failEvent);
	}

	public List<FailEvent> findAll() {
		return failEventRepository.findTop20ByStatusOrderByCreatedAtDesc(EventStatus.FAILED);
	}
}
