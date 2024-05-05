package com.team33.modulecore.item.dto.query;

import com.querydsl.core.annotations.QueryProjection;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ItemQueryDto {

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
        int reviewSize
    ) {
        this.itemId = itemId;
        this.thumbnail = thumbnail;
        this.productName = productName;
        this.mainFunction = mainFunction;
        this.baseStandard = baseStandard;
        this.realPrice = realPrice;
        this.discountRate = discountRate;
        this.discountPrice = discountPrice;
        this.sales = sales;
        this.starAvg = starAvg;
        this.reviewSize = reviewSize;
    }
}
