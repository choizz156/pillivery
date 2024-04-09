package com.team33.modulecore.domain.order.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import com.team33.modulecore.domain.order.entity.OrderItem;
import com.team33.modulecore.domain.order.entity.OrderStatus;

public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {

    @Query("select io from ITEM_ORDERS io join ORDERS o on io.order.orderId = o.orderId " +
            "and o.orderStatus = :orderStatus and o.user.userId = :userId and io.subscription = true")
    Page<OrderItem> findAllSubs(Pageable pageable, OrderStatus orderStatus, long userId);
}
