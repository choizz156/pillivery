package com.team33.modulecore.core.order.domain.repository;

import java.util.Optional;

import org.springframework.data.repository.Repository;

import com.team33.modulecore.core.order.domain.entity.Order;

public interface OrderCommandRepository extends Repository<Order, Long> {

    Order save(Order order);
    Optional<Order> findById(Long id);
    void delete(Order entity);
    void saveAll(Iterable<Order> entities);
}
