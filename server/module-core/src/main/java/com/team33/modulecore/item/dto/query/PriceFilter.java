package com.team33.modulecore.item.dto.query;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
public class PriceFilter {

    private int lowPrice;
    private int highPrice;

    @Builder
    public PriceFilter(int lowPrice, int highPrice) {
        this.lowPrice = lowPrice;
        this.highPrice = highPrice;
    }


    public boolean isSamePriceEach() {
        return this.lowPrice == this.highPrice && this.lowPrice != 0;
    }

    public void checkReversedPrice() {
        if (this.lowPrice > this.highPrice) {
            changePrice();
        }
    }

    private void changePrice() {
        int tmp = this.lowPrice;
        this.lowPrice = this.highPrice;
        this.highPrice = tmp;
    }

    public boolean isSumZero() {
        return lowPrice + highPrice == 0;
    }
}
