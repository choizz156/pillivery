package com.team33.modulecore.item.dto;

import com.team33.modulecore.category.domain.Category;
import com.team33.modulecore.category.domain.CategoryName;
import com.team33.modulecore.item.domain.Brand;
import com.team33.modulecore.item.domain.Item;
import java.util.List;
import java.util.stream.Collectors;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ItemResponseDto { // 목록 조회

    private Long itemId;
    private String thumbnail;
    private String title;
    private String content;
    private int capacity;
    private int realPrice;
    private double discountRate;
    private int discountPrice;
    private double starAvg;
    private int reviewSize;
    private List<CategoryName> categoryName;
    private Brand brand;
    private List<NutritionFactResponseDto> nutritionFacts;

    @Builder
    private ItemResponseDto(
        Long itemId,
        String thumbnail,
        String title,
        String content,
        int capacity,
        int price,
        double discountRate,
        int discountPrice,
        double starAvg,
        int reviewSize,
        List<CategoryName> categoryName,
        Brand brand,
        List<NutritionFactResponseDto> nutritionFacts
    ) {
        this.itemId = itemId;
        this.thumbnail = thumbnail;
        this.title = title;
        this.content = content;
        this.capacity = capacity;
        this.realPrice = price;
        this.discountRate = discountRate;
        this.discountPrice = discountPrice;
        this.starAvg = starAvg;
        this.reviewSize = reviewSize;
        this.categoryName = categoryName;
        this.brand = brand;
        this.nutritionFacts = nutritionFacts;
    }

    public static List<ItemResponseDto> from(List<Item> items) {
        return items.stream()
            .map(ItemResponseDto::from)
            .collect(Collectors.toUnmodifiableList());
    }

    public static ItemResponseDto from(Item item) {
        return ItemResponseDto.builder()
            .itemId(item.getId())
            .thumbnail(item.getThumbnail())
            .title(item.getTitle())
            .content(item.getContent())
            .capacity(item.getCapacity())
            .price(item.getOriginalPrice())
            .discountRate(item.getDiscountRate())
            .discountPrice(item.getDiscountPrice())
            .starAvg(item.getStarAvg())
            .reviewSize(item.getReviews().size())
            .categoryName(
                item.getCategories().stream()
                    .map(Category::getCategoryName)
                    .collect(Collectors.toUnmodifiableList())
            )
            .brand(item.getBrand())
            .nutritionFacts(
                item.getNutritionFacts().stream()
                    .map(NutritionFactResponseDto::from)
                    .collect(Collectors.toUnmodifiableList())
            )
            .build();
    }
}
