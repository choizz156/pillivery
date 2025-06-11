package com.team33.modulecore.core.item.dto.query;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

@EqualsAndHashCode
@NoArgsConstructor
@Getter
public class PriceFilter {

    private int Low = 0;
    private int High = 0;

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

    public boolean isSumZero() {
        return Low + High == 0;
    }

    private void changePrice() {
        int tmp = this.Low;
        this.Low = this.High;
        this.High = tmp;
    }
}
