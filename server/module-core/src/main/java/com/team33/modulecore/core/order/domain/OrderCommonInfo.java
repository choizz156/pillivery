package com.team33.modulecore.core.order.domain;

import java.time.ZonedDateTime;
import java.util.List;

import javax.persistence.Embeddable;
import javax.persistence.Embedded;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;

import com.team33.modulecore.core.order.domain.entity.OrderItem;
import com.team33.modulecore.core.order.dto.OrderContext;
import com.team33.modulecore.exception.BusinessLogicException;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@NoArgsConstructor
@ToString
@EqualsAndHashCode
@Getter
@Embeddable
public class OrderCommonInfo {

	private String mainItemName;

	private int totalQuantity;

	@Embedded
	private PaymentToken paymentToken;

	@Embedded
	private Price price;

	@Embedded
	private Receiver receiver;

	@Enumerated(EnumType.STRING)
	private OrderStatus orderStatus = OrderStatus.REQUEST;

	private Long userId;

	@Builder(toBuilder = true)
	public OrderCommonInfo(
		String mainItemName,
		int totalQuantity,
		PaymentToken paymentToken,
		Price price,
		Receiver receiver,
		OrderStatus orderStatus,
		Long userId
	) {

		this.mainItemName = mainItemName;
		this.totalQuantity = totalQuantity;
		this.paymentToken = paymentToken;
		this.price = price;
		this.receiver = receiver;
		this.orderStatus = orderStatus;
		this.userId = userId;
	}

	public static OrderCommonInfo createFromContext(List<OrderItem> orderItems, OrderContext orderContext) {

		return OrderCommonInfo.builder()
			.mainItemName(orderItems.get(0).getProductName())
			.totalQuantity(orderItems.stream()
				.map(OrderItem::getQuantity)
				.mapToInt(Integer::intValue)
				.sum())
			.paymentToken(new PaymentToken())
			.receiver(orderContext.getReceiver())
			.userId(orderContext.getUserId())
			.orderStatus(OrderStatus.REQUEST)
			.build();
	}

	public OrderCommonInfo addSid(String sid) {

		String tid = paymentToken.getTid();

		if (tid == null) {
			throw new BusinessLogicException("tid는 null일 수 없습니다.");
		}

		return this.toBuilder()
			.paymentToken(new PaymentToken(sid, tid))
			.build();
	}

	public OrderCommonInfo addTid(String tid) {

		return this.toBuilder()
			.paymentToken(new PaymentToken(null, tid))
			.build();
	}

	public OrderCommonInfo changeOrderStatus(OrderStatus orderStatus) {

		return this.toBuilder()
			.orderStatus(orderStatus)
			.build();
	}

	public void addPrice(List<OrderItem> orderItems) {
		this.price = new Price(orderItems);
	}

	public OrderCommonInfo adjustPriceAndTotalQuantity(List<OrderItem> orderItems) {

		int newTotalQuantity = countTotalQuantity(orderItems);
		
		return this.toBuilder()
			.price(new Price(orderItems))
			.totalQuantity(newTotalQuantity)
			.build();
	}

	public int getTotalPrice() {

		return this.price.getTotalPrice();
	}

	public String getSid() {

		return this.paymentToken.getSid();
	}

	public String getTid() {

		return this.paymentToken.getTid();
	}

	public OrderCommonInfo cancelSubscription() {
		return this.toBuilder()
			.orderStatus(OrderStatus.SUBSCRIBE_CANCEL)
			.build();
	}

	private int countTotalQuantity(List<OrderItem> orderItems) {

		if (orderItems.isEmpty()) {
			return 0;
		}

		return orderItems.stream()
			.map(OrderItem::getQuantity)
			.mapToInt(Integer::intValue)
			.sum();
	}
}
