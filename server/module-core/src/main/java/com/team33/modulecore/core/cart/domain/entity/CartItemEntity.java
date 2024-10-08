package com.team33.modulecore.core.cart.domain.entity;

import java.util.Objects;

import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.proxy.HibernateProxy;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.team33.modulecore.core.common.BaseEntity;
import com.team33.modulecore.core.item.domain.entity.Item;
import com.team33.modulecore.core.order.domain.SubscriptionInfo;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "cart_item")
@Entity
public class CartItemEntity extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "cart_item_id")
	private Long id;

	private int totalQuantity;

	@Embedded
	private SubscriptionInfo subscriptionInfo;

	@ManyToOne
	@JsonBackReference
	@JoinColumn(name = "cart_id")
	private CartEntity cartEntity;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "item_id")
	private Item item;


	@Builder
	public CartItemEntity(
		Long id,
		int totalQuantity,
		SubscriptionInfo subscriptionInfo,
		Item item
	) {
		this.id = id;
		this.totalQuantity = totalQuantity;
		this.subscriptionInfo = subscriptionInfo;
		this.item = item;
	}

	public static CartItemEntity of(Item item, int totalQuantity, SubscriptionInfo subscriptionInfo) {
		return CartItemEntity.builder()
			.totalQuantity(totalQuantity)
			.item(item)
			.subscriptionInfo(subscriptionInfo)
			.build();
	}

	public static CartItemEntity of(Item item, int totalQuantity) {
		return CartItemEntity.builder()
			.totalQuantity(totalQuantity)
			.item(item)
			.subscriptionInfo(SubscriptionInfo.of(false, 0))
			.build();
	}

	public int getPeriod() {
		return subscriptionInfo.getPeriod();
	}

	public boolean getSubscription() {
		return subscriptionInfo.isSubscription();
	}

	public void changeQuantity(int quantity) {
		this.totalQuantity = quantity;
	}

	public void changePeriod(int period) {
		this.subscriptionInfo = SubscriptionInfo.of(true, period);
	}

	public void addCart(CartEntity cartEntity) {
		this.cartEntity = cartEntity;
	}

	public void remove(CartEntity cartEntity) {
		this.cartEntity = null;
	}

	@Override
	public String toString() {
		return getClass().getSimpleName() + "(" +
			"id = " + id + ", " +
			"totalQuantity = " + totalQuantity + ", " +
			"subscriptionInfo = " + subscriptionInfo + ")";
	}

	@Override
	public final boolean equals(Object o) {
		if (this == o)
			return true;
		if (o == null)
			return false;
		Class<?> oEffectiveClass = o instanceof HibernateProxy ?
			((HibernateProxy)o).getHibernateLazyInitializer().getPersistentClass() : o.getClass();
		Class<?> thisEffectiveClass = this instanceof HibernateProxy ?
			((HibernateProxy)this).getHibernateLazyInitializer().getPersistentClass() : this.getClass();
		if (thisEffectiveClass != oEffectiveClass)
			return false;
		CartItemEntity cartItemEntity = (CartItemEntity)o;
		return getId() != null && Objects.equals(getId(), cartItemEntity.getId());
	}

	@Override
	public final int hashCode() {
		return this instanceof HibernateProxy ?
			((HibernateProxy)this).getHibernateLazyInitializer().getPersistentClass().hashCode() :
			getClass().hashCode();
	}
}
