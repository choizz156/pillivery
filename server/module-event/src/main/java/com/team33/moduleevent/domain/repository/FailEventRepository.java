package com.team33.moduleevent.domain.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.team33.moduleevent.domain.entity.FailEvent;

public interface FailEventRepository extends JpaRepository<FailEvent, Long> {
}
