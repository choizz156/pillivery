package com.team33.modulecore.core.item.dto.query;

import java.io.Serializable;

import com.querydsl.core.annotations.QueryProjection;
import com.team33.modulecore.core.category.domain.Categories;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;


@Getter
@NoArgsConstructor
public class ItemQueryDto implements Serializable {

    private Long itemId;
    private String thumbnail;
    private String productName;
    private String enterprise;
    private String mainFunction;
    private String baseStandard;
    private int realPrice;
    private double discountRate;
    private int discountPrice;
    private int sales;
    private double starAvg;
    private int reviewSize;
    private Categories categories;

    @QueryProjection
    @Builder
    public ItemQueryDto(
        Long itemId,
        String thumbnail,
        String productName,
        String enterprise,
        String mainFunction,
        String baseStandard,
        int realPrice,
        double discountRate,
        int discountPrice,
        int sales,
        double starAvg,
        int reviewSize,
        Categories categories
    ) {
        this.itemId = itemId;
        this.thumbnail = thumbnail;
        this.productName = productName;
        this.enterprise = enterprise;
        this.mainFunction = mainFunction;
        this.baseStandard = baseStandard;
        this.realPrice = realPrice;
        this.discountRate = discountRate;
        this.discountPrice = discountPrice;
        this.sales = sales;
        this.starAvg = starAvg;
        this.reviewSize = reviewSize;
        this.categories = categories;
    }
}
