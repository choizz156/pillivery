package com.team33.modulecore.cart.domain;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;

import com.team33.modulecore.item.domain.entity.Item;
import com.team33.modulecore.itemcart.domain.NormalCartItem;

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

	private int totalItems;

	private int totalPrice;

	private int totalDiscountPrice;

	@ElementCollection
	@CollectionTable(name = "normal_cart_item", joinColumns = @JoinColumn(name = "cart_id"))
	private List<NormalCartItem> normalCartItems = new ArrayList<>();

	public void addItem(Item item, int quantity){
		this.normalCartItems.add(NormalCartItem.of(item, quantity));
	}

	// 회원 한 명이 하나의 장바구니를 가지므로 회원당 1회만 장바구니 생성
	public static NormalCart create() {
		return new NormalCart();
	}

	public void changeTotalPrice(int totalPrice) {
		this.totalPrice = totalPrice;
	}

	public void changeTotalItems(int totalItems) {
		this.totalItems = totalItems;
	}
}
