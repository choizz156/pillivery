package com.team33.moduleevent.domain.repository;

import java.util.List;

import org.springframework.data.repository.Repository;

import com.team33.moduleevent.domain.EventStatus;
import com.team33.moduleevent.domain.entity.FailEvent;

public interface FailEventRepository extends Repository<FailEvent, Long> {

	void save(FailEvent event);
	List<FailEvent> findTop20ByStatusOrderByCreatedAt(EventStatus eventStatus);
}
