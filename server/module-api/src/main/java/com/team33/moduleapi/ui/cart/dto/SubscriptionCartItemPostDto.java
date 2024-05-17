package com.team33.moduleapi.ui.cart.dto;

import lombok.Data;

@Data
public class SubscriptionCartItemPostDto {
	private Long itemId;

	private int period;
	private int quantity;
	private boolean isSubscription = true;
}
