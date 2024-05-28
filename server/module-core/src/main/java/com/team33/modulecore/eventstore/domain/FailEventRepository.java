package com.team33.modulecore.eventstore.domain;

import java.util.List;

import org.springframework.data.repository.Repository;

public interface FailEventRepository extends Repository<FailEvent, Long> {
	void save(FailEvent event);

	List<FailEvent> findTop20ByStatusOrderByCreatedAtDesc(EventStatus eventStatus);
}
