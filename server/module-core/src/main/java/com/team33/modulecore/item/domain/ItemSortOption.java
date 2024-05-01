package com.team33.modulecore.item.domain;

import static com.team33.modulecore.item.domain.entity.QItem.item;

import com.querydsl.core.types.OrderSpecifier;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ItemSortOption {
    SALES(item.sales.desc(), "판매량 순 "),
    PRICE_H(item.itemPrice.realPrice.desc(), "높은 가격 순"),
    PRICE_L(item.itemPrice.realPrice.asc(), "낮은 가격 순"),
    DISCOUNT_RATE_H(item.itemPrice.discountRate.desc(), "높은 할인율 순"),
    DISCOUNT_RATE_L(item.itemPrice.discountRate.asc(), "낮은 할인율 순");

    private final OrderSpecifier<? extends Number> sort;
    private final String description;
}
