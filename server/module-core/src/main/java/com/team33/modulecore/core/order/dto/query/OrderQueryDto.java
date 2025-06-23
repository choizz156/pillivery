package com.team33.modulecore.core.order.dto.query;

import com.querydsl.core.annotations.QueryProjection;
import com.team33.modulecore.core.order.domain.OrderStatus;
import com.team33.modulecore.core.order.domain.Receiver;
import java.time.ZonedDateTime;
import java.util.List;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
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

	private List<OrderItemSimpleQueryDto> orderItemSimpleQueryDtos;

	@QueryProjection
	@Builder
	public OrderQueryDto(
		long orderId,
		int totalItems,
		int totalPrice,
		int totalDiscountPrice,
		int expectPrice,
		OrderStatus orderStatus,
		ZonedDateTime createdAt,
		ZonedDateTime updatedAt,
		Receiver receiver,
		int totalQuantity
	) {

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
	}
}
