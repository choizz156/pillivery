package com.team33.modulecore.cart.domain;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import com.team33.modulecore.common.BaseEntity;
import com.team33.modulecore.item.domain.entity.Item;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@EqualsAndHashCode
@Embeddable
public class NormalCartItem extends BaseEntity {

    @Column(nullable = false)
    private int totalQuantity;

    @ManyToOne
    @JoinColumn(name = "item_id")
    private Item item;

    private NormalCartItem(int totalQuantity, Item item) {
        this.totalQuantity = totalQuantity;
        this.item = item;
    }

    public static NormalCartItem of(Item item, int quantity) {
        return new NormalCartItem(quantity, item);
    }

	public void changeQuantity(int quantity) {
       this.totalQuantity = quantity;
	}
}
