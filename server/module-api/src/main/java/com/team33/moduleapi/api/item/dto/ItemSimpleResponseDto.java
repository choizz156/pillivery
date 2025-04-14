package com.team33.moduleapi.api.item.dto;

import com.team33.modulecore.core.cart.vo.ItemVO;
import com.team33.modulecore.core.item.domain.entity.Item;
import com.team33.modulecore.core.item.dto.query.ItemQueryDto;

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

    public static ItemSimpleResponseDto of(ItemQueryDto item) {
        return ItemSimpleResponseDto.builder()
            .itemId(item.getItemId())
            .enterprise(item.getEnterprise())
            .thumbnail(item.getThumbnail())
            .product(item.getProductName())
            .discountRate(item.getDiscountRate())
            .discountPrice(item.getDiscountPrice())
            .build();
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

    public static ItemSimpleResponseDto of(ItemVO item) {

        if (item == null) {
            return new ItemSimpleResponseDto();
        }

        return ItemSimpleResponseDto.builder()
            .itemId(item.getId())
            .enterprise(item.getEnterprise())
            .thumbnail(item.getThumbnailUrl())
            .product(item.getProductName())
            .originPrice(item.getOriginPrice())
            .discountRate(item.getDiscountRate())
            .discountPrice(item.getDiscountPrice())
            .build();
    }
}
