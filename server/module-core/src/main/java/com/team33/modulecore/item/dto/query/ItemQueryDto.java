package com.team33.modulecore.item.dto.query;

import com.team33.modulecore.category.domain.CategoryName;
import com.team33.modulecore.item.domain.Brand;
import com.team33.modulecore.item.domain.entity.NutritionFact;
import java.util.List;
import lombok.AccessLevel;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ItemQueryDto {

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
    private Brand brand;
    private List<CategoryName> categoryNames;
    private List<NutritionFact> nutritionFacts;

}
