package com.team33.modulecore.item.dto;

import com.team33.modulecore.category.domain.Category;
import com.team33.modulecore.category.domain.CategoryName;
import com.team33.modulecore.item.domain.Brand;
import com.team33.modulecore.item.domain.Item;
import com.team33.modulecore.review.dto.ReviewResponseDto;
import com.team33.modulecore.talk.dto.TalkAndCommentDto;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ItemDetailResponse { // 아이템 상세 조회

    private Long itemId;
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
    private List<CategoryName> categories;
    private Set<NutritionFactResponseDto> nutritionFacts;
    private double starAvg;
    private List<ReviewResponseDto> reviews;
    private List<TalkAndCommentDto> talks;

    @Builder
    private ItemDetailResponse(
        Long itemId,
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
        List<CategoryName> categories,
        Set<NutritionFactResponseDto> nutritionFacts,
        double starAvg,
        List<ReviewResponseDto> reviews,
        List<TalkAndCommentDto> talks
    ) {
        this.itemId = itemId;
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
        this.reviews = reviews;
        this.talks = talks;
    }

    public static ItemDetailResponse of(Item item) {
        return ItemDetailResponse.builder()
            .itemId(item.getId())
            .thumbnail(item.getThumbnail())
            .descriptionImage(item.getDescriptionImage())
            .title(item.getTitle())
            .content(item.getContent())
            .expiration(item.getExpiration())
            .brand(item.getBrand())
            .sales(item.getSales())
            .price(item.getPrice())
            .capacity(item.getCapacity())
            .servingSize(item.getServingSize())
            .discountRate(item.getDiscountRate())
            .discountPrice(item.getDiscountPrice())
            .starAvg(item.getStarAvg())
            .categories(
                item.getCategories()
                    .stream()
                    .map(Category::getCategoryName)
                    .collect(Collectors.toUnmodifiableList())
            )
            .nutritionFacts(item.getNutritionFacts().stream()
                .map(nutritionFact ->
                    new NutritionFactResponseDto(
                        nutritionFact.getIngredient(),
                        nutritionFact.getIngredient()
                    )
                ).collect(Collectors.toSet())
            )
            .build();

        //TODO: review는 리팩토링 하면서 추가
    }
}
