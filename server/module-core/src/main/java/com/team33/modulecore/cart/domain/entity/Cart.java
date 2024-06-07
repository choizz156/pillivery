package com.team33.modulecore.cart.domain.entity;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.DiscriminatorColumn;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.OneToMany;

import com.team33.modulecore.cart.domain.CartPrice;
import com.team33.modulecore.common.BaseEntity;
import com.team33.modulecore.item.domain.entity.Item;

import lombok.Getter;

@Getter
@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "dtype")
public abstract class Cart extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "cart_id")
	private Long id;

	@Embedded
	CartPrice price = new CartPrice(0, 0, 0);

	@OneToMany(mappedBy = "cart", cascade = CascadeType.ALL, orphanRemoval = true)
	List<CartItem> cartItems = new ArrayList<>();

	public void removeCartItem(CartItem removedItem) {
		this.cartItems.remove(removedItem);

		Item item = removedItem.getItem();
		int quantity = removedItem.getTotalQuantity();

		this.price = this.price.subtractPriceInfo(item.getRealPrice(), item.getDiscountPrice(), quantity);
	}

	public void changeCartItemQuantity(CartItem cartItem, int quantity) {
		Item item = cartItem.getItem();

		this.price = CartPrice.of(item.getRealPrice(), item.getDiscountPrice(), quantity);
		cartItem.changeQuantity(quantity);
	}

	public int getTotalDiscountPrice() {
		return this.price.getTotalDiscountPrice();
	}

	public int getTotalPrice() {
		return this.price.getTotalPrice();
	}

	public int getTotalItemCount() {
		return this.price.getTotalItemCount();
	}

	public int getExpectedPrice() {
		return this.price.getTotalPrice() - this.price.getTotalDiscountPrice();
	}

}
