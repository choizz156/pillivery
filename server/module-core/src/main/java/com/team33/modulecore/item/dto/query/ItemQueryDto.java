package com.team33.modulecore.item.dto.query;

import com.team33.modulecore.category.domain.CategoryName;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ItemQueryDto {

    private Long itemId;
    private String thumbnail;
    private String title;
    private String content;
    private int capacity;
    private int realPrice;
    private double discountRate;
    private int discountPrice;
    private int sales;
    private double starAvg;
    private int reviewSize;
    private CategoryName categoryNames;

    @Builder
    public ItemQueryDto(
        Long itemId,
        String thumbnail,
        String title,
        String content,
        int capacity,
        int realPrice,
        double discountRate,
        int discountPrice,
        double starAvg,
        int reviewSize
    ) {
        this.itemId = itemId;
        this.thumbnail = thumbnail;
        this.title = title;
        this.content = content;
        this.capacity = capacity;
        this.realPrice = realPrice;
        this.discountRate = discountRate;
        this.discountPrice = discountPrice;
        this.starAvg = starAvg;
        this.reviewSize = reviewSize;
        this.categoryNames = categoryNames;
    }

}
