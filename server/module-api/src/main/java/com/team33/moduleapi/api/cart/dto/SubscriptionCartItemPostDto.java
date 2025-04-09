package com.team33.moduleapi.api.cart.dto;

import lombok.Data;


@Data
public class SubscriptionCartItemPostDto {
	private Long itemId;
	private int period;
	private int quantity;
	private boolean subscription = true;
}
