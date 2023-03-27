package server.team33.domain.item.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import server.team33.domain.category.dto.CategoryDto;
import server.team33.domain.nutritionFact.dto.NutritionFactDto.Post;
import server.team33.domain.nutritionFact.dto.NutritionFactDto.Response;
import server.team33.global.response.MultiResponseDto;
import server.team33.domain.talk.dto.TalkAndCommentDto;
import server.team33.domain.item.entity.Brand;
import server.team33.domain.review.dto.ReviewResponseDto;

import java.util.List;

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
        private List<CategoryDto.Post> categories;
        private List<Post> nutritionFacts;
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
        private MultiResponseDto<ReviewResponseDto> reviews;
        private MultiResponseDto<TalkAndCommentDto> talks;
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
        private MultiResponseDto<ItemCategoryResponse> bestItem;
        private MultiResponseDto<ItemCategoryResponse> saleItem;
        private MultiResponseDto<ItemCategoryResponse> MdPickItem;
    }
}
