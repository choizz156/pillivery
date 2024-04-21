package com.team33.modulecore.order.repository;

import com.team33.modulecore.order.domain.Order;
import com.team33.modulecore.order.dto.OrderFindCondition;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface OrderRepositoryCustom {

    Page<Order> searchOrders(
        Pageable pageRequest,
        OrderFindCondition orderFindCondition
    );

}
