package com.team33.modulecore.eventstore.domain;

import java.util.List;

import org.springframework.data.repository.Repository;

public interface EventRepository extends Repository<ApiEventSet, Long> {

	List<ApiEventSet> findTop20ByStatusOrderByCreatedAtDesc(EventStatus eventStatus);
	void save(ApiEventSet event);
}
