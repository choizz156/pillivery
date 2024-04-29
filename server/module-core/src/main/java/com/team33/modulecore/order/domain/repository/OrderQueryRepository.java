package com.team33.modulecore.order.domain.repository;

import com.team33.modulecore.order.domain.Order;
import com.team33.modulecore.order.domain.OrderItem;
import com.team33.modulecore.order.dto.OrderFindCondition;
import com.team33.modulecore.order.dto.OrderPageRequest;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.repository.Repository;

public interface OrderQueryRepository extends Repository<Order, Long> {

    Page<Order> searchOrders(
        OrderPageRequest pageRequest,
        OrderFindCondition orderFindCondition
    );

    List<OrderItem> findSubscriptionOrderItem(
        OrderPageRequest pageRequest,
        OrderFindCondition orderFindCondition
    );

    Order findById(Long id);
}
