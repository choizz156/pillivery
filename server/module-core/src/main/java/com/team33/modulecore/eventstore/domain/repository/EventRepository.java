package com.team33.modulecore.eventstore.domain.repository;

import java.util.List;

import org.springframework.data.repository.Repository;

import com.team33.modulecore.eventstore.domain.EventStatus;
import com.team33.modulecore.eventstore.domain.entity.ApiEventSet;

public interface EventRepository extends Repository<ApiEventSet, Long> {

	List<ApiEventSet> findTop20ByStatusOrderByCreatedAtDesc(EventStatus eventStatus);
	void save(ApiEventSet event);
}
