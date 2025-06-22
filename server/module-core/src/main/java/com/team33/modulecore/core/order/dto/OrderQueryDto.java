package com.team33.modulecore.core.order.dto;

import java.time.ZonedDateTime;

import com.querydsl.core.annotations.QueryProjection;
import com.team33.modulecore.core.order.domain.OrderStatus;
import com.team33.modulecore.core.order.domain.Receiver;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class OrderQueryDto {

	private long orderId;
	private int totalItems;
	private int totalPrice;
	private int totalDiscountPrice;
	private int expectPrice;
	private OrderStatus orderStatus;
	private ZonedDateTime createdAt;
	private ZonedDateTime updatedAt;
	private Receiver receiver;
	private int totalQuantity;
	private long itemId;
	private String enterprise;
	private String product;
	private int originPrice;
	private int realPrice;
	private double discountRate;
	private int discountPrice;

	@QueryProjection
	@Builder
	public OrderQueryDto(long orderId, int totalItems, int totalPrice, int totalDiscountPrice, int expectPrice,
		OrderStatus orderStatus, ZonedDateTime createdAt, ZonedDateTime updatedAt, Receiver receiver, int totalQuantity,
		 long itemId, String enterprise, String product, int originPrice, int realPrice,
		double discountRate, int discountPrice) {

		this.orderId = orderId;
		this.totalItems = totalItems;
		this.totalPrice = totalPrice;
		this.totalDiscountPrice = totalDiscountPrice;
		this.expectPrice = expectPrice;
		this.orderStatus = orderStatus;
		this.createdAt = createdAt;
		this.updatedAt = updatedAt;
		this.receiver = receiver;
		this.totalQuantity = totalQuantity;
		this.itemId = itemId;
		this.enterprise = enterprise;
		this.product = product;
		this.originPrice = originPrice;
		this.realPrice = realPrice;
		this.discountRate = discountRate;
		this.discountPrice = discountPrice;
	}
}
