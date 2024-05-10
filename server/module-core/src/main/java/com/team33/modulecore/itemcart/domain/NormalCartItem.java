package com.team33.modulecore.itemcart.domain;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import com.team33.modulecore.common.BaseEntity;
import com.team33.modulecore.item.domain.entity.Item;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@Embeddable
public class NormalCartItem extends BaseEntity {

    @Column(nullable = false)
    private int quantity;

    @ManyToOne
    @JoinColumn(name = "item_id")
    private Item item;

    private NormalCartItem(int quantity, Item item) {
        this.quantity = quantity;
        this.item = item;
    }

    public static NormalCartItem of(Item item, int quantity) {
        return new NormalCartItem(quantity, item);
    }

    public void addQuantity(int quantity) {
        this.quantity += quantity;
    }
}
