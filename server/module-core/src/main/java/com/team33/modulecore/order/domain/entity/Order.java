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
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.proxy.HibernateProxy;

import com.team33.modulecore.common.BaseEntity;
import com.team33.modulecore.exception.BusinessLogicException;
import com.team33.modulecore.item.domain.entity.Item;
import com.team33.modulecore.order.domain.OrderItem;
import com.team33.modulecore.order.domain.OrderPrice;
import com.team33.modulecore.order.domain.OrderStatus;
import com.team33.modulecore.order.domain.PaymentCode;
import com.team33.modulecore.order.domain.Receiver;
import com.team33.modulecore.order.dto.OrderContext;

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

	private boolean isSubscription;

	@Column(name = "cart_request")
	private boolean isOrderedAtCart;

	private int totalItemsCount;

	private String mainItemName;

	private int totalQuantity;

	@Embedded
	private PaymentCode paymentCode;

	@Embedded
	private OrderPrice orderPrice;

	@Embedded
	private Receiver receiver;

	@Enumerated(EnumType.STRING)
	private OrderStatus orderStatus = OrderStatus.REQUEST;

	private Long userId;

	@OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<OrderItem> orderItems = new ArrayList<>();

	public Order(Order origin) {
		this.isSubscription = origin.isSubscription();
		this.isOrderedAtCart = origin.isOrderedAtCart();
		this.totalItemsCount = origin.getTotalItemsCount();
		this.totalQuantity = origin.getTotalQuantity();
		this.paymentCode = origin.getPaymentCode();
		this.orderPrice = origin.getOrderPrice();
		this.receiver = origin.getReceiver();
		this.orderStatus = origin.getOrderStatus();
		this.userId = origin.getUserId();
		this.orderItems = origin.getOrderItems();
		this.mainItemName = origin.getMainItemName();
		this.userId = origin.getUserId();
	}

	@Builder
	public Order(
		boolean isSubscription,
		boolean isOrderedAtCart,
		int totalItemsCount,
		String mainItemName,
		PaymentCode paymentCode,
		OrderPrice orderPrice,
		Receiver receiver,
		OrderStatus orderStatus,
		int totalQuantity,
		Long userId,
		List<OrderItem> orderItems
	) {
		this.isSubscription = isSubscription;
		this.isOrderedAtCart = isOrderedAtCart;
		this.totalItemsCount = totalItemsCount;
		this.mainItemName = mainItemName;
		this.paymentCode = paymentCode;
		this.orderPrice = orderPrice;
		this.receiver = receiver;
		this.orderStatus = orderStatus;
		this.totalQuantity = totalQuantity;
		this.userId = userId;
		this.orderItems = orderItems;
	}

	public static Order create(List<OrderItem> orderItems, OrderContext orderContext) {
		Order order = Order.builder()
			.receiver(orderContext.getReceiver())
			.isSubscription(orderContext.isSubscription())
			.isOrderedAtCart(orderContext.isOrderedCart())
			.userId(orderContext.getUserId())
			.orderStatus(OrderStatus.REQUEST)
			.orderItems(orderItems)
			.totalItemsCount(orderItems.size())
			.totalQuantity(orderItems.stream().mapToInt(OrderItem::getQuantity).sum())
			.mainItemName(orderItems.get(0).getItem().getProductName())
			.build();

		order.addPrice(order.getOrderItems());
		order.getOrderItems().forEach(orderItem -> orderItem.addOrder(order));
		return order;
	}

	public void addSid(String sid) {
		String tid = paymentCode.getTid();

		if (tid == null) {
			throw new BusinessLogicException("tid는 null일 수 없습니다.");
		}

		this.paymentCode = PaymentCode.addSid(tid, sid);
	}

	public void addTid(String tid) {
		this.paymentCode = PaymentCode.addTid(tid);
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

	public void adjustPriceAndTotalQuantity(List<OrderItem> orderItems) {
		this.orderPrice = new OrderPrice(orderItems);
		countTotalQuantity();
	}

	public int getTotalPrice() {
		return this.orderPrice.getTotalPrice();
	}

	public String getSid() {
		return this.paymentCode.getSid();
	}

	public String getTid() {
		return this.paymentCode.getTid();
	}

	private void countTotalQuantity() {

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
