package com.team33.moduleapi.ui.cart.dto;

import java.time.ZonedDateTime;

import com.team33.moduleapi.ui.item.dto.ItemSimpleResponseDto;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class CartItemResponseDto {

	private int quantity;
	private int period;
	private boolean subscription;
	private ItemSimpleResponseDto item;
	private ZonedDateTime createdAt;
	private ZonedDateTime updatedAt;

	@Builder
	private CartItemResponseDto(
		int quantity,
		int period,
		boolean subscription,
		ItemSimpleResponseDto item,
		ZonedDateTime createdAt,
		ZonedDateTime updatedAt
	) {
		this.quantity = quantity;
		this.period = period;
		this.item = item;
		this.createdAt = createdAt;
		this.updatedAt = updatedAt;
		this.subscription = subscription;
	}
}
