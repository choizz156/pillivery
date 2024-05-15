package com.team33.modulecore.payment.application;

import com.team33.modulecore.order.domain.Order;

public interface PayRequest<T> {

    T requestOneTime(Order order);
    T requestSubscription(Order order);
}
