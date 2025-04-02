package com.team33.modulecore.core.order.domain.entity;

import java.time.ZonedDateTime;
import java.util.Collections;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import com.team33.modulecore.core.common.BaseEntity;
import com.team33.modulecore.core.order.domain.OrderCommonInfo;
import com.team33.modulecore.core.order.domain.OrderStatus;
import com.team33.modulecore.core.order.domain.SubscriptionInfo;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;


@ToString(exclude = "orderItem")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "subscription_order")
@Entity
public class SubscriptionOrder extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "subscription_order_id")
	private Long id;

	@Embedded
	private OrderCommonInfo orderCommonInfo;

	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "order_item_id")
	private OrderItem orderItem;

	@Builder
	public SubscriptionOrder(
		Long id,
		OrderItem orderItem,
		OrderCommonInfo orderCommonInfo
	) {
		this.id = id;
		this.orderItem = orderItem;
		this.orderCommonInfo = orderCommonInfo;
	}

	public static SubscriptionOrder create(Order order, OrderItem orderItem) {

		SubscriptionOrder subscriptionOrder = SubscriptionOrder.builder()
			.orderItem(orderItem)
			.orderCommonInfo(order.getOrderCommonInfo())
			.build();

		subscriptionOrder.getOrderCommonInfo().addPrice(List.of(orderItem));
		orderItem.addSubscriptionOrder(subscriptionOrder);
		return subscriptionOrder;
	}

	public static SubscriptionOrder create(OrderCommonInfo orderCommonInfo, OrderItem orderItem) {

		SubscriptionOrder subscriptionOrder = SubscriptionOrder.builder()
			.orderItem(orderItem)
			.orderCommonInfo(orderCommonInfo)
			.build();

		subscriptionOrder.getOrderCommonInfo().addPrice(List.of(orderItem));
		orderItem.addSubscriptionOrder(subscriptionOrder);
		return subscriptionOrder;
	}

	public void adjustPriceAndTotalQuantity(List<OrderItem> orderItems) {
		this.orderCommonInfo = this.orderCommonInfo.adjustPriceAndTotalQuantity(orderItems);
	}

	public void cancelSubscription() {
		this.orderCommonInfo = this.orderCommonInfo.cancelSubscription();
		if (this.orderItem != null) {
			cancelSubscribeOrderItem();
		}
	}

	public int getPeriod() {
		return this.orderItem.getSubscriptionInfo().getPeriod();
	}

	public boolean isSubscription() {
		return this.orderItem.getSubscriptionInfo().isSubscription();
	}

	public void updateSubscriptionPaymentDay(ZonedDateTime paymentDay) {
		this.orderItem.getSubscriptionInfo().updatePaymentDay(paymentDay);
	}

	public ZonedDateTime getNextPaymentDay() {
		return this.orderItem.getSubscriptionInfo().getNextPaymentDay();
	}

	public void changePeriod(int newPeriod) {
		this.orderItem.getSubscriptionInfo().changePeriod(newPeriod);
	}

	public void changeOrderStatus(OrderStatus orderStatus) {
		this.orderCommonInfo = this.orderCommonInfo.changeOrderStatus(orderStatus);
	}

	public void addSid(String sid) {
		this.orderCommonInfo = this.orderCommonInfo.addSid(sid);
	}

	public void addTid(String tid) {
		this.orderCommonInfo = this.orderCommonInfo.addTid(tid);
	}

	public int getTotalQuantity() {
		return this.orderCommonInfo.getTotalQuantity();
	}

	public int getTotalPrice() {
		return this.orderCommonInfo.getTotalPrice();
	}

	public String getMainItemName() {
		return this.orderCommonInfo.getMainItemName();
	}

	public String getSid() {
		return this.orderCommonInfo.getSid();
	}

	public List<Long> getItemId() {
		return Collections.singletonList(this.orderItem.getItem().getId());
	}

	private void cancelSubscribeOrderItem() {
		SubscriptionInfo subscriptionInfo = this.orderItem.getSubscriptionInfo();
		if (subscriptionInfo != null) {
			subscriptionInfo.cancelSubscription();
		}
	}
}
