package com.team33.modulecore.item.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ItemSortOption {
    SALES("판매량"),
    PRICE_H("최고가"),
    PRICE_L("최저가"),
    DISCOUNT_RATE_H("할인율 높음"),
    DISCOUNT_RATE_L("할인율 낮음");

    private final String options;
}
