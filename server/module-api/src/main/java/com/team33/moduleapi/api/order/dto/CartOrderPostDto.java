package com.team33.moduleapi.api.order.dto;

import java.util.List;

import lombok.Data;

@Data
public class CartOrderPostDto {
	private List<OrderPostDto> orderPostDtoList;
	private boolean isSubscription;
}
