package com.team33.modulecore.order.domain.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.Repository;
import org.springframework.data.repository.query.Param;

import com.team33.modulecore.order.domain.entity.Order;

public interface OrderCommandRepository extends Repository<Order, Long> {

    Order save(Order order);
    Optional<Order> findById(Long id);
    void delete(Order entity);
}
