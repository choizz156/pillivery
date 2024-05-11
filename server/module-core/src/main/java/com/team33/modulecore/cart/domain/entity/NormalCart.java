package com.team33.modulecore.cart.domain.entity;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;

import com.team33.modulecore.cart.domain.CartPrice;
import com.team33.modulecore.cart.domain.NormalCartItem;
import com.team33.modulecore.item.domain.entity.Item;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class NormalCart {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "cart_id")
	private Long id;

	@Embedded
	private CartPrice price;

	@ElementCollection
	@CollectionTable(name = "normal_cart_item", joinColumns = @JoinColumn(name = "cart_id"))
	private List<NormalCartItem> normalCartItems = new ArrayList<>();

	public void addItem(Item item, int quantity) {
		this.normalCartItems.add(NormalCartItem.of(item, quantity));
		this.price = this.price.addPrice(item.getRealPrice(), item.getDiscountPrice(), quantity);
	}

	public static NormalCart create() {
		return new NormalCart();
	}

	public void removeCartItem(NormalCartItem removedItem) {
		this.normalCartItems.remove(removedItem);

		Item item = removedItem.getItem();
		int quantity = removedItem.getTotalQuantity();

		this.price = this.price.subtractPrice(item.getRealPrice(), item.getDiscountPrice(), quantity);
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

	public int getExpectedPrice(){
		return this.price.getTotalPrice() - this.price.getTotalDiscountPrice();
	}
}
