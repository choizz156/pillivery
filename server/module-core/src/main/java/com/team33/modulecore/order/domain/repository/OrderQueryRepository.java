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

    Order findById(long id);
    boolean findIsSubscriptionById(@Param("orderId") long orderId);
    String findTid(@Param("orderId") long orderId);
}
