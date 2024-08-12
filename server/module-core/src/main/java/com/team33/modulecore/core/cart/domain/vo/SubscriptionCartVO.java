package com.team33.modulecore.core.cart.domain.vo;

import java.util.Objects;

import org.hibernate.proxy.HibernateProxy;

import com.team33.modulecore.core.item.domain.entity.Item;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class SubscriptionCartVO extends CartVO {

	public static SubscriptionCartVO create() {
		return new SubscriptionCartVO();
	}

	public void addSubscriptionItem(CartItemVO cartItem) {
		cartItem.addCart(this);
		super.cartItems.add(cartItem);

		Item item = cartItem.getItem();
		super.price = super.price.addPriceInfo(
			item.getRealPrice(),
			item.getDiscountPrice(),
			cartItem.getTotalQuantity()
		);
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
		SubscriptionCartVO that = (SubscriptionCartVO)o;
		return getId() != null && Objects.equals(getId(), that.getId());
	}

	@Override
	public final int hashCode() {
		return this instanceof HibernateProxy ?
			((HibernateProxy)this).getHibernateLazyInitializer().getPersistentClass().hashCode() :
			getClass().hashCode();
	}
}
