package com.team33.modulecore.core.order.dto;

import com.team33.modulecore.core.order.domain.OrderStatus;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class OrderFindCondition {

	private long userId;
	private Boolean subscription;
	private OrderStatus orderStatus;

	@Builder
	private OrderFindCondition(long userId, boolean isSubscription, OrderStatus orderStatus) {
		this.userId = userId;
		this.subscription = isSubscription;
		this.orderStatus = orderStatus;
	}

	public static OrderFindCondition to(long userId, OrderStatus orderStatus) {
		return OrderFindCondition.builder()
			.userId(userId)
			.orderStatus(orderStatus)
			.build();
	}

}
