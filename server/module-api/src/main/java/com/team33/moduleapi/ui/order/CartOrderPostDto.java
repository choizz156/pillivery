package com.team33.moduleapi.ui.order;

import java.util.List;

import com.team33.moduleapi.ui.order.dto.OrderPostDto;

import lombok.Data;

@Data
public class CartOrderPostDto {
	private List<OrderPostDto> orderPostDtoList;
	private boolean isSubscription;
}
