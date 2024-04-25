package com.team33.modulecore.item.dto;

import com.team33.modulecore.category.domain.CategoryName;
import com.team33.modulecore.item.domain.Brand;
import java.util.Set;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ItemPostServiceDto {

    private String thumbnail;
    private String descriptionImage;
    private String title;
    private String content;
    private String expiration;
    private Brand brand;
    private int sales;
    private int price;
    private int capacity;
    private int servingSize;
    private int discountRate;
    private int discountPrice;
    private Set<CategoryName> categories;
    private Set<NutritionFactPostDto> nutritionFacts;
    private double starAvg;

    @Builder
    private ItemPostServiceDto(
        String thumbnail,
        String descriptionImage,
        String title,
        String content,
        String expiration,
        Brand brand,
        int sales,
        int price,
        int capacity,
        int servingSize,
        int discountRate,
        int discountPrice,
        Set<CategoryName> categories,
        Set<NutritionFactPostDto> nutritionFacts,
        double starAvg
    ) {
        this.thumbnail = thumbnail;
        this.descriptionImage = descriptionImage;
        this.title = title;
        this.content = content;
        this.expiration = expiration;
        this.brand = brand;
        this.sales = sales;
        this.price = price;
        this.capacity = capacity;
        this.servingSize = servingSize;
        this.discountRate = discountRate;
        this.discountPrice = discountPrice;
        this.categories = categories;
        this.nutritionFacts = nutritionFacts;
        this.starAvg = starAvg;
    }

    public static ItemPostServiceDto to(ItemPostDto itemPostDto) {
        return ItemPostServiceDto.builder()
            .thumbnail(itemPostDto.getThumbnail())
            .descriptionImage(itemPostDto.getDescriptionImage())
            .title(itemPostDto.getTitle())
            .content(itemPostDto.getContent())
            .expiration(itemPostDto.getExpiration())
            .brand(itemPostDto.getBrand())
            .sales(itemPostDto.getSales())
            .price(itemPostDto.getPrice())
            .capacity(itemPostDto.getCapacity())
            .servingSize(itemPostDto.getServingSize())
            .discountRate(itemPostDto.getDiscountRate())
            .discountPrice(itemPostDto.getDiscountPrice())
            .categories(itemPostDto.getCategories())
            .nutritionFacts(itemPostDto.getNutritionFacts())
            .starAvg(itemPostDto.getStarAvg())
            .build();
    }
}
