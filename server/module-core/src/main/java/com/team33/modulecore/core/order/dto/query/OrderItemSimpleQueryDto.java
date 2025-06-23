package com.team33.modulecore.core.order.dto.query;

import com.querydsl.core.annotations.QueryProjection;
import lombok.Data;

@Data
public class OrderItemSimpleQueryDto {

	private long orderItemId;
	private int quantity;
	//item
	private long itemId;
	private String enterprise;
	private String product;
	private int originPrice;
	private int realPrice;
	private double discountRate;
	private int discountPrice;


	@QueryProjection
	public OrderItemSimpleQueryDto(long orderItemId, int quantity, long itemId, String enterprise,
		String product, int originPrice, int realPrice, double discountRate, int discountPrice) {
		this.orderItemId = orderItemId;
		this.quantity = quantity;
		this.itemId = itemId;
		this.enterprise = enterprise;
		this.product = product;
		this.originPrice = originPrice;
		this.realPrice = realPrice;
		this.discountRate = discountRate;
		this.discountPrice = discountPrice;
	}
}
