package com.team33.modulecore.item.dto;

import com.team33.modulecore.item.domain.Brand;
import com.team33.modulecore.item.domain.Item;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ItemSimpleResponseDto {

    private long itemId;
    private Brand brand;
    private String thumbnail;
    private String title;
    private int capacity;
    private int price;
    private double discountRate;
    private int disCountPrice;

    @Builder
    private ItemSimpleResponseDto(
        long itemId,
        Brand brand,
        String thumbnail,
        String title,
        int capacity,
        int price,
        double discountRate,
        int disCountPrice
    ) {
        this.itemId = itemId;
        this.brand = brand;
        this.thumbnail = thumbnail;
        this.title = title;
        this.capacity = capacity;
        this.price = price;
        this.discountRate = discountRate;
        this.disCountPrice = disCountPrice;
    }

    public static ItemSimpleResponseDto of(Item item) {
        return ItemSimpleResponseDto.builder()
            .itemId(item.getId())
            .brand(item.getBrand())
            .thumbnail(item.getThumbnail())
            .title(item.getTitle())
            .capacity(item.getCapacity())
            .price(item.getOriginalPrice())
            .discountRate(item.getItemPrice().getDiscountRate())
            .disCountPrice(item.getDiscountPrice()).
            build();
    }
}
