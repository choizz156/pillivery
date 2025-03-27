package com.team33.modulecore.core.order.domain.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import com.team33.modulecore.core.common.BaseEntity;
import com.team33.modulecore.core.item.domain.entity.Item;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "order_item")
@Entity
public class OrderItem extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "order_item_id")
	private Long id;

	@Column(nullable = false)
	private int quantity;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "item_id")
	private Item item;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "order_id")
	private Order order;

	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "subscription_order_id")
	private SubscriptionOrder subscriptionOrder;

	@Builder
	private OrderItem(int quantity, Item item) {
		this.quantity = quantity;
		this.item = item;
	}

	public static OrderItem create(
		Item item,
		int quantity
	) {
		return OrderItem.builder()
			.item(item)
			.quantity(quantity)
			.build();
	}

	public void addOrder(Order order) {
		this.order = order;
	}

	public void addSubscriptionOrder(SubscriptionOrder subscriptionOrder) {
		this.subscriptionOrder = subscriptionOrder;
	}

	public void changeQuantity(int quantity) {
		this.quantity = quantity;
	}

	public boolean containsItem(Long id) {
		return item.getId().equals(id);
	}

	public String getProductName() {
		return item.getProductName();
	}
}
