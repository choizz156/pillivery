package com.team33.modulecore.core.item.dto;

import com.team33.modulecore.core.order.domain.OrderStatus;
import com.team33.modulecore.core.user.domain.entity.User;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ItemFindCondition {

    private User user;
    private Boolean subscription;
    private OrderStatus orderStatus;

    @Builder
    private ItemFindCondition(User user, boolean isSubscription, OrderStatus orderStatus) {
        this.user = user;
        this.subscription = isSubscription;
        this.orderStatus = orderStatus;
    }

    public static ItemFindCondition to(User user, OrderStatus orderStatus) {
        return ItemFindCondition.builder()
            .user(user)
            .orderStatus(orderStatus)
            .build();
    }

}
