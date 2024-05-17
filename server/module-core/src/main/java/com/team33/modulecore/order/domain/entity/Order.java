package com.team33.modulecore.order.domain.entity;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.proxy.HibernateProxy;

import com.team33.modulecore.common.BaseEntity;
import com.team33.modulecore.item.domain.entity.Item;
import com.team33.modulecore.order.domain.OrderItem;
import com.team33.modulecore.order.domain.OrderPrice;
import com.team33.modulecore.order.domain.OrderStatus;
import com.team33.modulecore.order.domain.Receiver;
import com.team33.modulecore.order.dto.OrderContext;
import com.team33.modulecore.user.domain.User;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@DynamicUpdate
@Entity(name = "orders")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Order extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "order_id")
	private Long id;

	private boolean isSubscription;

	@Column(name = "cart_request")
	private boolean isOrderedAtCart;

	private int totalItemsCount;

	private int totalQuantity;

	private String sid;

	@Embedded
	private OrderPrice orderPrice;

	@Embedded
	private Receiver receiver;

	@Enumerated(EnumType.STRING)
	private OrderStatus orderStatus = OrderStatus.REQUEST;


	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "user_id")
	private User user;

	@OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<OrderItem> orderItems = new ArrayList<>();

	public Order(Order origin) {
		this.isSubscription = origin.isSubscription();
		this.isOrderedAtCart = origin.isOrderedAtCart();
		this.totalItemsCount = origin.getTotalItemsCount();
		this.totalQuantity = origin.getTotalQuantity();
		this.sid = origin.getSid();
		this.orderPrice = 	origin.getOrderPrice();
		this.receiver = origin.getReceiver();
		this.orderStatus = origin.getOrderStatus();
		this.user = origin.getUser();
		this.orderItems = origin.getOrderItems();
	}

	@Builder
	public Order(
		boolean isSubscription,
		boolean isOrderedAtCart,
		int totalItemsCount,
		OrderPrice orderPrice,
		Receiver receiver,
		OrderStatus orderStatus,
		int totalQuantity,
		User user,
		List<OrderItem> orderItems
	) {
		this.isSubscription = isSubscription;
		this.isOrderedAtCart = isOrderedAtCart;
		this.totalItemsCount = totalItemsCount;
		this.orderPrice = orderPrice;
		this.receiver = receiver;
		this.orderStatus = orderStatus;
		this.totalQuantity = totalQuantity;
		this.user = user;
		this.orderItems = orderItems;
	}

	public static Order create(List<OrderItem> orderItems, OrderContext orderContext, User user) {
		Order order = Order.builder()
			.receiver(orderContext.getReceiver())
			.isSubscription(orderContext.isSubscription())
			.isOrderedAtCart(orderContext.isOrderedCart())
			.user(user)
			.orderStatus(OrderStatus.REQUEST)
			.orderItems(orderItems)
			.totalItemsCount(orderItems.size())
			.totalQuantity(orderItems.stream().mapToInt(OrderItem::getQuantity).sum())
			.build();

		order.addPrice(order.getOrderItems());
		order.getOrderItems().forEach(orderItem -> orderItem.addOrder(order));
		return order;
	}

	public void addSid(String sid) {
		this.sid = sid;
	}

	public String getOrdererCity() {
		return this.user.getCityAtAddress();
	}

	public String getOrdererDetailAddress() {
		return this.user.getDetailAddress();
	}

	public Item getFirstItem() {
		return orderItems.get(0).getItem();
	}

	public void changeOrderStatus(OrderStatus orderStatus) {
		this.orderStatus = orderStatus;
	}

	public void addPrice(List<OrderItem> orderItems) {
		this.orderPrice = new OrderPrice(orderItems);
	}

	public void adjustPriceAndQuantity(List<OrderItem> orderItems) {
		this.orderPrice = new OrderPrice(orderItems);
		countQuantity();
	}

	public String getFirstProductName() {
		return this.orderItems.get(0).getItem().getProductName();
	}

	public int getTotalPrice() {
		return this.orderPrice.getTotalPrice();
	}

	private void countQuantity() {

		if (this.orderItems.isEmpty()) {
			this.totalQuantity = 0;
			return;
		}

		this.totalQuantity =
			this.orderItems.stream()
				.map(OrderItem::getQuantity)
				.reduce(0, Integer::sum);
	}

	@Override
	public final boolean equals(Object object) {
		if (this == object)
			return true;
		if (object == null)
			return false;
		Class<?> oEffectiveClass = object instanceof HibernateProxy ?
			((HibernateProxy)object).getHibernateLazyInitializer().getPersistentClass() :
			object.getClass();
		Class<?> thisEffectiveClass = this instanceof HibernateProxy ?
			((HibernateProxy)this).getHibernateLazyInitializer().getPersistentClass() : this.getClass();
		if (thisEffectiveClass != oEffectiveClass)
			return false;
		Order order = (Order)object;
		return getId() != null && Objects.equals(getId(), order.getId());
	}

	@Override
	public final int hashCode() {
		return this instanceof HibernateProxy ?
			((HibernateProxy)this).getHibernateLazyInitializer().getPersistentClass().hashCode() :
			getClass().hashCode();
	}
}
