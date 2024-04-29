package com.team33.modulecore.order.domain.repository;


import com.team33.modulecore.order.domain.Order;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderRepository extends JpaRepository<Order, Long> {
}
