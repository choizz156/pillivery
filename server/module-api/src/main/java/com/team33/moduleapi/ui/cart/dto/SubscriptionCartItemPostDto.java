package com.team33.moduleapi.ui.cart.dto;

import lombok.Data;

@Data
public class SubscriptionCartItemPostDto {

	private int period;

	private boolean isSubscription = true;
}
