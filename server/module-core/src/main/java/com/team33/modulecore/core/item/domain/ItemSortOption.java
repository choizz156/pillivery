package com.team33.modulecore.core.item.domain;


import static com.team33.modulecore.core.item.domain.entity.QItem.*;

import com.querydsl.core.types.OrderSpecifier;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ItemSortOption {
    SALES(item.statistics.sales.desc(), "판매량 순 "),
    PRICE_H(item.information.price.realPrice.desc(), "높은 가격 순"),
    PRICE_L(item.information.price.realPrice.asc(), "낮은 가격 순"),
    DISCOUNT_RATE_H(item.information.price.discountRate.desc(), "높은 할인율 순"),
    DISCOUNT_RATE_L(item.information.price.discountRate.asc(), "낮은 할인율 순"),
    REVIEW_COUNT(item.statistics.reviewCount.desc(), "리뷰 개수 순");

    private final OrderSpecifier<? extends Number> sort;
    private final String description;
}
