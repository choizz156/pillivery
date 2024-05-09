package com.team33.modulecore.itemcart.dto;

import javax.validation.constraints.Min;

import lombok.Data;

@Data
public class SubscriptionItemPostDto {

	private int period;

	private boolean isSubscription = true;
}
