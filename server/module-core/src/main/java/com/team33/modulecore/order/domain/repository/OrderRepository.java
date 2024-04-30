package com.team33.modulecore.order.domain.repository;


import com.team33.modulecore.order.domain.Order;
import java.util.Optional;
import org.springframework.data.repository.Repository;

public interface OrderRepository extends Repository<Order, Long> {

    Order save(Order order);
    Optional<Order> findById(Long id);
    void delete(Order entity);
}
