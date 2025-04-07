package com.team33.modulecore.core.order.dto.query;

import com.querydsl.core.annotations.QueryProjection;
import com.team33.modulecore.core.item.domain.entity.Item;
import com.team33.modulecore.core.order.domain.entity.OrderItem;
import com.team33.modulecore.core.order.domain.entity.SubscriptionOrder;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
public class SubscriptionOrderItemQueryDto {

	private Long subscriptionOrderId;
	private Long orderItemId;
	private int quantity;
	private Long itemId;
	private String itemName;
	private int price;
	private String description;
	private String imageUrl;
	private String category;

	@QueryProjection
	@Builder
	public SubscriptionOrderItemQueryDto(SubscriptionOrder subscriptionOrder, OrderItem orderItem, Item item) {
		this.subscriptionOrderId = subscriptionOrder.getId();
		this.orderItemId = orderItem.getId();
		this.quantity = orderItem.getQuantity();
		this.itemId = item.getId();
		this.itemName = item.getProductName();
		this.price = item.getRealPrice();
		this.description = item.getDescriptionImage();
		this.imageUrl = item.getThumbnailUrl();
		this.category = item.getCategories() != null ? item.getCategories().getCategoryNameSet().toString() : "";
	}
}