package com.team33.modulecore.order.dto;

import com.team33.modulecore.order.domain.OrderStatus;
import com.team33.modulecore.user.domain.User;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class OrderFindCondition {

    private User user;
    private boolean isSubscription;
    private OrderStatus orderStatus;

    @Builder
    private OrderFindCondition(User user, boolean isSubscription, OrderStatus orderStatus) {
        this.user = user;
        this.isSubscription = isSubscription;
        this.orderStatus = orderStatus;
    }

    public static OrderFindCondition to(User user, boolean subscription, OrderStatus orderStatus) {
        return OrderFindCondition.builder()
            .isSubscription(subscription)
            .user(user)
            .orderStatus(orderStatus)
            .build();
    }
}
