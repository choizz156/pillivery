package com.team33.modulecore.item.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
public class PriceFilterDto {

    private int lowPrice;
    private int highPrice;

    private PriceFilterDto(int lowPrice, int highPrice) {
        this.lowPrice = lowPrice;
        this.highPrice = highPrice;
    }

    public static PriceFilterDto to(ItemPriceDto dto) {
        return new PriceFilterDto(dto.getLow(), dto.getHigh());
    }

    public boolean isSamePriceEach() {
        return this.lowPrice == this.highPrice;
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
}
