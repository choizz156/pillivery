package com.team33.modulecore.core.payment.domain.entity;

import java.time.ZonedDateTime;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToOne;

import com.team33.modulecore.core.order.domain.OrderStatus;
import com.team33.modulecore.core.order.domain.PaymentId;
import com.team33.modulecore.core.order.domain.Price;
import com.team33.modulecore.core.order.domain.Receiver;
import com.team33.modulecore.core.order.domain.SubscriptionInfo;
import com.team33.modulecore.core.order.domain.entity.Order;
import com.team33.modulecore.core.order.domain.entity.OrderItem;
import com.team33.modulecore.exception.BusinessLogicException;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class SubscriptionOrder {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "subscription_order_id")
	private Long id;

	private String mainItemName;

	private int totalAmount;

	@Embedded
	private SubscriptionInfo subscriptionInfo;

	@Embedded
	private PaymentId paymentId;

	@Embedded
	private Receiver receiver;

	@Embedded
	private Price price;

	@Enumerated(EnumType.STRING)
	private OrderStatus orderStatus = OrderStatus.REQUEST;

	private Long userId;

	@OneToOne(mappedBy = "subscriptionOrder", cascade = CascadeType.ALL, orphanRemoval = true)
	private OrderItem orderItem;

	@Builder
	public SubscriptionOrder(
		Long id,
		String mainItemName,
		int totalAmount,
		PaymentId paymentId,
		Receiver receiver,
		OrderStatus orderStatus,
		Long userId,
		OrderItem orderItem,
		SubscriptionInfo subscriptionInfo
	) {
		this.id = id;
		this.mainItemName = mainItemName;
		this.totalAmount = totalAmount;
		this.paymentId = paymentId;
		this.receiver = receiver;
		this.orderStatus = orderStatus;
		this.userId = userId;
		this.orderItem = orderItem;
		this.subscriptionInfo = subscriptionInfo;
	}

	public static SubscriptionOrder create(Order order, OrderItem orderItem) {

		SubscriptionOrder subscriptionOrder = SubscriptionOrder.builder()
			.receiver(order.getReceiver())
			.userId(order.getUserId())
			.orderStatus(OrderStatus.SUBSCRIBE)
			.orderItem(orderItem)
			.totalAmount(orderItem.getQuantity())
			.mainItemName(orderItem.getItem().getProductName())
			.subscriptionInfo(order.getSubscriptionInfo())
			.build();

		subscriptionOrder.addPrice(List.of(orderItem));
		subscriptionOrder.getOrderItem().addSubscriptionOrder(subscriptionOrder);
		return subscriptionOrder;
	}

	public void addPrice(List<OrderItem> orderItems) {
		this.price = new Price(orderItems);
	}

	public void adjustPriceAndTotalQuantity(List<OrderItem> orderItems) {
		this.price = new Price(orderItems);
		countTotalAmount();
	}

	public void cancelSubscription() {
		this.subscriptionInfo.cancelSubscription();
		this.orderStatus = OrderStatus.SUBSCRIBE_CANCEL;
	}

	public int getPeriod() {
		return subscriptionInfo.getPeriod();
	}

	public boolean isSubscription() {
		return subscriptionInfo.isSubscription();
	}

	public void updateSubscriptionPaymentDay(ZonedDateTime paymentDay) {
		this.subscriptionInfo.updatePaymentDay(paymentDay);
	}

	public ZonedDateTime getNextPaymentDay() {
		return subscriptionInfo.getNextPaymentDay();
	}

	public void changePeriod(int newPeriod) {
		this.subscriptionInfo.changePeriod(newPeriod);
	}

	public void setPriceToZero() {
		this.price = new Price(List.of());
	}

	public void changeOrderStatus(OrderStatus orderStatus) {
		this.orderStatus = orderStatus;
	}

	public void addSid(String sid) {
		String tid = paymentId.getTid();

		if (tid == null) {
			throw new BusinessLogicException("tid는 null일 수 없습니다.");
		}

		this.paymentId = new PaymentId(sid, tid);
	}

	public void addTid(String tid) {
		this.paymentId = new PaymentId(null, tid);
	}

	private void countTotalAmount() {

		if (this.orderItem == null) {
			this.totalAmount = 0;
			return;
		}

		this.totalAmount = orderItem.getQuantity();
	}
}
