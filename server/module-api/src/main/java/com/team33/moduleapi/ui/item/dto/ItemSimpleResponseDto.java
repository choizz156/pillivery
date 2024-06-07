package com.team33.moduleapi.ui.item.dto;

import com.team33.modulecore.item.domain.entity.Item;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ItemSimpleResponseDto {

    private long itemId;
    private String enterprise;
    private String thumbnail;
    private String product;
    private int originPrice;
    private int realPrice;
    private double discountRate;
    private int discountPrice;

    @Builder
    public ItemSimpleResponseDto(
        long itemId,
        String enterprise,
        String thumbnail,
        String product,
        int originPrice,
        int realPrice,
        double discountRate,
        int discountPrice
    ) {
        this.itemId = itemId;
        this.enterprise = enterprise;
        this.thumbnail = thumbnail;
        this.product = product;
        this.originPrice = originPrice;
        this.realPrice = realPrice;
        this.discountRate = discountRate;
        this.discountPrice = discountPrice;
    }

    public static ItemSimpleResponseDto of(Item item) {
        return ItemSimpleResponseDto.builder()
            .itemId(item.getId())
            .enterprise(item.getInformation().getEnterprise())
            .thumbnail(item.getThumbnailUrl())
            .product(item.getProductName())
            .originPrice(item.getOriginPrice())
            .discountRate(item.getDiscountRate())
            .discountPrice(item.getDiscountPrice())
            .build();
    }
}
