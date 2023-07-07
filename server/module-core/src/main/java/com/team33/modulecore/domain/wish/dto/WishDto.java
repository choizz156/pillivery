package com.team33.modulecore.domain.wish.dto;

import com.team33.modulecore.domain.nutritionFact.dto.NutritionFactDto.Response;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import com.team33.modulecore.domain.item.entity.Brand;

public class WishDto {

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class WishItemResponseDto {

        private long itemId;
        private String thumbnail;
        private String title;
        private String content;
        private int capacity;
        private int price;
        private int discountRate;
        private int discountPrice;
        private Brand brand;
        private List<Response> nutritionFacts;
        private double starAvg;
        private int reviewSize;
    }


    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class WishResponseDto {

        private long itemId;
        private int wish;
        private int totalWishes;
    }

}
