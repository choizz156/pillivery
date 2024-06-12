package com.team33.moduleevent.domain.repository;

import java.util.List;

import org.springframework.data.repository.Repository;

import com.team33.moduleevent.domain.EventStatus;
import com.team33.moduleevent.domain.entity.ApiEvent;

public interface EventRepository extends Repository<ApiEvent, Long> {

	void save(ApiEvent event);
	List<ApiEvent> findTop20ByStatusOrderByCreatedAt(EventStatus eventStatus);
}
