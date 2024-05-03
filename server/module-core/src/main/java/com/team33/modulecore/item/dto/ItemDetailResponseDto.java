package com.team33.modulecore.item.dto;

import com.team33.modulecore.category.domain.CategoryName;
import com.team33.modulecore.item.domain.entity.Item;
import com.team33.modulecore.review.dto.ReviewResponseDto;
import java.util.List;
import java.util.stream.Collectors;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ItemDetailResponseDto { // 아이템 상세 조회

    private Long itemId;
    private String thumbnail;
    private String descriptionImage;
    private String title;
    private String content;
    private String expiration;
    private int sales;
    private int realPrice;
    private int capacity;
    private String servingSize;
    private double discountRate;
    private int discountPrice;
    private List<CategoryName> categories;
    private double starAvg;
    private List<ReviewResponseDto> reviews;

    @Builder
    private ItemDetailResponseDto(
        Long itemId,
        String thumbnail,
        String descriptionImage,
        String title,
        String content,
        String expiration,
        int sales,
        int price,
        int capacity,
        String servingSize,
        double discountRate,
        int discountPrice,
        List<CategoryName> categories,
        double starAvg,
        List<ReviewResponseDto> reviews
    ) {
        this.itemId = itemId;
        this.thumbnail = thumbnail;
        this.descriptionImage = descriptionImage;
        this.title = title;
        this.content = content;
        this.expiration = expiration;
        this.sales = sales;
        this.realPrice = price;
        this.capacity = capacity;
        this.servingSize = servingSize;
        this.discountRate = discountRate;
        this.discountPrice = discountPrice;
        this.categories = categories;
        this.starAvg = starAvg;
        this.reviews = reviews;
    }

    public static ItemDetailResponseDto of(Item item) {
        return ItemDetailResponseDto.builder()
            .itemId(item.getId())
            .thumbnail(item.getThumbnailUrl())
            .descriptionImage(item.getDescriptionImage())
            .title(item.getProductName())
            .expiration(item.getInformation().getPreservePeriod())
            .sales(item.getSales())
            .price(item.getOriginPrice())
            .servingSize(item.getServingUse())
            .discountRate(item.getDiscountRate())
            .discountPrice(item.getDiscountPrice())
            .starAvg(item.getStarAvg())
            .categories(
                item.getItemCategories()
                    .stream()
                    .map(itemCategory -> itemCategory.getCategory().getCategoryName())
                    .collect(Collectors.toUnmodifiableList())
            )
//            .reviews(item.getItemReviewsBy5().stream()
//                .map(ReviewResponseDto::from)
//                .collect(Collectors.toList())
//            )
            .build();
    }
}
