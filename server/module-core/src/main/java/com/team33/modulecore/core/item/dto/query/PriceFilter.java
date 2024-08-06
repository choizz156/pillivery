package com.team33.modulecore.core.item.dto.query;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
public class PriceFilter {

    private int Low;
    private int High;

    @Builder
    public PriceFilter(int Low, int High) {
        this.Low = Low;
        this.High = High;
    }


    public boolean isSamePriceEach() {
        return this.Low == this.High && this.Low != 0;
    }

    public void checkReversedPrice() {
        if (this.Low > this.High) {
            changePrice();
        }
    }

    private void changePrice() {
        int tmp = this.Low;
        this.Low = this.High;
        this.High = tmp;
    }

    public boolean isSumZero() {
        return Low + High == 0;
    }
}
