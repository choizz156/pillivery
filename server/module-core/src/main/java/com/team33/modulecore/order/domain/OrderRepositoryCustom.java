package com.team33.modulecore.order.domain;

import com.team33.modulecore.order.infra.OrderPageRequest;
import com.team33.modulecore.user.domain.User;
import org.springframework.data.domain.Page;

public interface OrderRepositoryCustom {

    Page<Order> findPaidOrders(
        OrderPageRequest pageRequest,
        User user,
        OrderStatus orderStatus,
        boolean subscription
    );
}
