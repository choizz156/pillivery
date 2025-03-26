package com.team33.modulebatch.domain;

import org.springframework.data.jpa.repository.JpaRepository;

import com.team33.modulebatch.domain.entity.ErrorItem;

public interface ErrorItemRepository extends JpaRepository<ErrorItem, Long> {

}
