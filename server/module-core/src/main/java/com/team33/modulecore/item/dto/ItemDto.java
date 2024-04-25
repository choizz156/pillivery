package com.team33.modulecore.item.dto;

import com.team33.modulecore.item.domain.Brand;
import java.util.List;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

public class ItemDto {


    @Getter
    @Setter
    @NoArgsConstructor
    public static class ItemCategoryResponse { // 목록 조회
        private Long itemId;
        private String thumbnail;
        private String title;
        private String content;
        private int capacity;
        private int price;
        private int discountRate;
        private int discountPrice;
        private double starAvg;
        private int reviewSize;
        private Brand brand;
        private List<NutritionFactResponseDto> nutritionFacts;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    public static class ItemMainTop9Response {
//        private MultiResponseDto<ItemCategoryResponse> bestItem;
//        private MultiResponseDto<ItemCategoryResponse> saleItem;
//        private MultiResponseDto<ItemCategoryResponse> MdPickItem;
    }
}
