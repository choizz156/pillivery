package com.team33.modulecore.item.domain;

import javax.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Embeddable
public class ItemPrice {

    private int originPrice;

    private int realPrice;

    private int discountPrice;

    private double discountRate;

    public ItemPrice(int originPrice, double discountRate) {
        this.originPrice = originPrice;
        this.discountRate = discountRate;
        this.discountPrice = originPrice - originPrice / (int) discountRate;
        this.realPrice = originPrice - discountPrice;
    }
}
