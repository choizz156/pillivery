package com.team33.modulecore.order.repository;

import com.team33.modulecore.order.domain.Order;
import com.team33.modulecore.order.dto.OrderFindCondition;
import com.team33.modulecore.order.dto.OrderPageRequest;
import com.team33.modulecore.orderitem.domain.OrderItem;
import java.util.List;
import org.springframework.data.domain.Page;

public interface OrderRepositoryCustom {


    Page<Order> searchOrders(
        OrderPageRequest pageRequest,
        OrderFindCondition orderFindCondition
    );

    List<OrderItem> findSubscriptionOrder(
        OrderPageRequest pageRequest,
        OrderFindCondition orderFindCondition
    );
}
