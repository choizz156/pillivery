package com.team33.modulecore.core.cart.domain.entity;

import java.util.List;
import java.util.Objects;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

import org.hibernate.proxy.HibernateProxy;

import com.team33.modulecore.core.cart.domain.CartPrice;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@DiscriminatorValue("subscription")
@Entity
public class SubscriptionCartEntity extends CartEntity {

	private SubscriptionCartEntity(Long id, CartPrice price, List<CartItemEntity> cartItemEntities) {
		super(id, price, cartItemEntities);
	}

	public static SubscriptionCartEntity create() {
		return new SubscriptionCartEntity();
	}

	public static SubscriptionCartEntity of(Long id, CartPrice price, List<CartItemEntity> cartItemEntities) {
		return new SubscriptionCartEntity(id, price, cartItemEntities);
	}

	@Override
	public final int hashCode() {
		return this instanceof HibernateProxy ?
			((HibernateProxy)this).getHibernateLazyInitializer().getPersistentClass().hashCode() :
			getClass().hashCode();
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
		SubscriptionCartEntity that = (SubscriptionCartEntity)o;
		return getId() != null && Objects.equals(getId(), that.getId());
	}
}
