package com.team33.moduleevent.domain.repository;

import java.util.List;

import org.springframework.data.repository.Repository;

import com.team33.modulecore.eventstore.domain.EventStatus;
import com.team33.moduleevent.domain.entity.ApiEventSet;

public interface EventRepository extends Repository<ApiEventSet, Long> {

	void save(ApiEventSet event);
	List<ApiEventSet> findTop20ByStatusOrderByCreatedAt(EventStatus eventStatus);
}
