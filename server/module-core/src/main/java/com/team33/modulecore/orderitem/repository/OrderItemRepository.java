package com.team33.modulecore.orderitem.repository;

import com.team33.modulecore.orderitem.domain.OrderItem;
import com.team33.modulecore.order.domain.OrderStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {

    @Query("select io from OrderItem io join orders o on io.order.orderId = o.orderId " +
            "and o.orderStatus = :orderStatus and o.user.id = :id and io.orderItemInfo.subscription = true")
    Page<OrderItem> findAllSubs(Pageable pageable, OrderStatus orderStatus, long id);
}
