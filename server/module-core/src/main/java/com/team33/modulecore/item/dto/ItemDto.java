package com.team33.modulecore.item.dto;

import com.team33.modulecore.category.dto.CategoryDto.Post;
import com.team33.modulecore.item.domain.Brand;
import com.team33.modulecore.nutritionFact.dto.NutritionFactDto;
import com.team33.modulecore.nutritionFact.dto.NutritionFactDto.Response;
import java.util.List;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

public class ItemDto {


    @Getter
    @Setter
    @NoArgsConstructor
    public static class post {

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
        private List<Post> categories;
        private List<NutritionFactDto.Post> nutritionFacts;
        private double starAvg;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    public static class ItemDetailResponse { // 아이템 상세 조회
        private Long ItemId;
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
        private List<String> categories;
        private List<Response> nutritionFacts;
        private double starAvg;
//        private MultiResponseDto<ReviewResponseDto> reviews;
//        private MultiResponseDto<TalkAndCommentDto> talks;
    }


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
        private List<Response> nutritionFacts;
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
