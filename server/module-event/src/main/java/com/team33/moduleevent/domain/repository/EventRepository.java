package com.team33.moduleevent.domain.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.team33.moduleevent.domain.EventStatus;
import com.team33.moduleevent.domain.EventType;
import com.team33.moduleevent.domain.entity.ApiEvent;

public interface EventRepository extends JpaRepository<ApiEvent, Long> {

	List<ApiEvent> findTop20ByStatusOrderByCreatedAt(EventStatus eventStatus);
    Optional<ApiEvent> findByTypeAndParameters(EventType subscriptionCanceled, String cancelParam);
}
