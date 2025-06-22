package com.team33.modulecore.core.order.dto.query;

import com.querydsl.core.annotations.QueryProjection;
import com.team33.modulecore.core.category.domain.Categories;
import com.team33.modulecore.core.item.domain.entity.Item;
import com.team33.modulecore.core.order.domain.entity.Order;
import com.team33.modulecore.core.order.domain.entity.OrderItem;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
public class OrderItemQueryDto {

	private Long orderId;
	private Long orderItemId;
	private int quantity;
	private Long itemId;
	private String itemName;
	private int price;
	private String description;
	private String imageUrl;
	private Categories category;

	@QueryProjection
	@Builder
	public OrderItemQueryDto(Long orderId, Long orderItemId, int quantity, Long itemId, String itemName, int price,
		String description, String imageUrl, Categories category) {

		this.orderId = orderId;
		this.orderItemId = orderItemId;
		this.quantity = quantity;
		this.itemId = itemId;
		this.itemName = itemName;
		this.price = price;
		this.description = description;
		this.imageUrl = imageUrl;
		this.category = category;
	}

	// @QueryProjection
	@Builder
	public OrderItemQueryDto(Order order, OrderItem orderItem, Item item) {

		this.orderId = order.getId();
		this.orderItemId = orderItem.getId();
		this.quantity = orderItem.getQuantity();
		this.itemId = item.getId();
		this.itemName = item.getProductName();
		this.price = item.getRealPrice();
		this.description = item.getDescriptionImage();
		this.imageUrl = item.getThumbnailUrl();
		// this.category = item.getCategories() != null ? item.getCategories().getCategoryNameSet().toString() : "";
	}
}