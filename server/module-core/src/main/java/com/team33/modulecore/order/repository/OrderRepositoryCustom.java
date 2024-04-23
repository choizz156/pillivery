package com.team33.modulecore.order.repository;

import com.team33.modulecore.order.domain.Order;
import com.team33.modulecore.order.dto.OrderFindCondition;
import com.team33.modulecore.order.dto.OrderPageRequest;
import java.util.List;
import org.springframework.data.domain.Page;

public interface OrderRepositoryCustom {


    Page<Order> searchOrders(
        OrderPageRequest pageRequest,
        OrderFindCondition orderFindCondition
    );

    List<Order> findSubscriptionOrder(
        OrderPageRequest pageRequest,
        OrderFindCondition orderFindCondition
    );
}
