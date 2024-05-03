package com.team33.modulecore.item.domain;

import javax.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Embeddable
public class Price {

    private int originPrice;

    private int realPrice;

    private int discountPrice;

    private Double discountRate;

    @Builder
    public Price(int originPrice, double discountRate) {
        this.originPrice = originPrice;
        this.discountRate = discountRate;
        this.discountPrice = originPrice - originPrice / (int) discountRate;
        this.realPrice = originPrice - discountPrice;
    }

}
