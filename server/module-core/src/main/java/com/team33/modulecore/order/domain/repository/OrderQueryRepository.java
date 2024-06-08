package com.team33.modulecore.order.domain.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.repository.query.Param;

import com.team33.modulecore.order.domain.OrderItem;
import com.team33.modulecore.order.domain.entity.Order;
import com.team33.modulecore.order.dto.OrderFindCondition;
import com.team33.modulecore.order.dto.OrderPageRequest;


public interface OrderQueryRepository{

    Page<Order> findOrders(
        OrderPageRequest pageRequest,
        OrderFindCondition orderFindCondition
    );

    List<OrderItem> findSubscriptionOrderItem(
        OrderPageRequest pageRequest,
        OrderFindCondition orderFindCondition
    );

    Order findById(Long id);
    boolean findIsSubscriptionById(@Param("orderId") Long orderId);
    String findTid(@Param("orderId") Long orderId);
}
