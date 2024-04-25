package com.team33.modulecore.item.dto;

import com.team33.modulecore.category.domain.CategoryName;
import com.team33.modulecore.item.domain.Brand;
import java.util.Set;
import javax.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ItemPostDto {

    private String thumbnail;
    private String descriptionImage;
    @NotNull
    private String title;
    @NotNull
    private String content;
    @NotNull
    private String expiration;
    @NotNull
    private Brand brand;
    @NotNull
    private int sales;
    @NotNull
    private int price;
    @NotNull
    private int capacity;
    @NotNull
    private int servingSize;
    @NotNull
    private int discountRate;
    @NotNull
    private int discountPrice;
    @NotNull
    private Set<CategoryName> categories;
    @NotNull
    private Set<NutritionFactPostDto> nutritionFacts;
    @NotNull
    private double starAvg;
}
