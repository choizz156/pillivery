package com.team33.modulecore.core.cart.domain.entity;

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
import javax.persistence.Table;

import com.team33.modulecore.core.cart.dto.CartPrice;
import com.team33.modulecore.core.common.BaseEntity;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@Entity
@Table(name = "cart")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "dtype")
public abstract class CartEntity extends BaseEntity {

	@Embedded
	CartPrice price = new CartPrice(0, 0, 0);

	@OneToMany(mappedBy = "cartEntity", cascade = CascadeType.ALL, orphanRemoval = true)
	List<CartItemEntity> cartItemEntities = new ArrayList<>();
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "cart_id")
	private Long id;

	public CartEntity(Long id, CartPrice price, List<CartItemEntity> cartItemEntities) {
		this.id = id;
		this.price = price;
		this.cartItemEntities = cartItemEntities;
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
