package com.team33.modulecore.core.order.domain.entity;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.proxy.HibernateProxy;

import com.team33.modulecore.core.common.BaseEntity;
import com.team33.modulecore.core.item.domain.entity.Item;
import com.team33.modulecore.core.order.domain.OrderCommonInfo;
import com.team33.modulecore.core.order.domain.OrderStatus;
import com.team33.modulecore.core.order.domain.PaymentId;
import com.team33.modulecore.core.order.domain.Price;
import com.team33.modulecore.core.order.domain.SubscriptionInfo;
import com.team33.modulecore.core.order.dto.OrderContext;
import com.team33.modulecore.exception.BusinessLogicException;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@DynamicUpdate
@Table(name = "orders")
@Entity
public class Order extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "order_id")
	private Long id;

	@Column(name = "cart_request")
	private boolean isOrderedAtCart;

	private int totalItemsCount;

	@Embedded
	private OrderCommonInfo orderCommonInfo;

	@OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<OrderItem> orderItems = new ArrayList<>();

	@Builder
	public Order(
		boolean isOrderedAtCart,
		OrderCommonInfo orderCommonInfo,
		int totalItemsCount,
		List<OrderItem> orderItems
	) {
		this.isOrderedAtCart = isOrderedAtCart;
		this.orderCommonInfo = orderCommonInfo;
		this.orderItems = orderItems;
		this.totalItemsCount = totalItemsCount;
	}

	public static Order create(
		List<OrderItem> orderItems,
		OrderCommonInfo orderCommonInfo,
		OrderContext orderContext
	) {
		Order order = Order.builder()
			.orderCommonInfo(orderCommonInfo)
			.isOrderedAtCart(orderContext.isOrderedCart())
			.orderItems(orderItems)
			.totalItemsCount(orderItems.size())
			.build();

		order.addPrice(order.getOrderItems());
		order.getOrderItems().forEach(orderItem -> orderItem.addOrder(order));
		return order;
	}

	public void addPrice(List<OrderItem> orderItems) {
		this.orderCommonInfo = this.orderCommonInfo.addPrice(orderItems);
	}

	public void adjustPriceAndTotalQuantity(List<OrderItem> orderItems) {
		this.orderCommonInfo = this.orderCommonInfo.adjustPriceAndTotalQuantity(orderItems);
	}

	public Item getFirstItem() {
		return orderItems.get(0).getItem();
	}


}
