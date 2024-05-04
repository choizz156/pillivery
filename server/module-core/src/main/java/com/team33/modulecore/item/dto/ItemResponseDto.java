package com.team33.modulecore.item.dto;

import com.team33.modulecore.category.domain.CategoryName;
import com.team33.modulecore.item.domain.entity.Item;
import com.team33.modulecore.item.dto.query.ItemQueryDto;
import java.util.List;
import java.util.stream.Collectors;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ItemResponseDto { // 목록 조회

    private Long itemId;
    private String thumbnail;
    private String productName;
    private String mainFunction;
    private String baseStandard;
    private int realPrice;
    private double discountRate;
    private int discountPrice;
    private double starAvg;
    private int reviewSize;
    private CategoryName categoryName;

    @Builder
    public ItemResponseDto(
        Long itemId,
        String thumbnail,
        String productName,
        String mainFunction,
        String baseStandard,
        int realPrice,
        double discountRate,
        int discountPrice,
        double starAvg,
        int reviewSize,
        CategoryName categoryName
    ) {
        this.itemId = itemId;
        this.thumbnail = thumbnail;
        this.productName = productName;
        this.mainFunction = mainFunction;
        this.baseStandard = baseStandard;
        this.realPrice = realPrice;
        this.discountRate = discountRate;
        this.discountPrice = discountPrice;
        this.starAvg = starAvg;
        this.reviewSize = reviewSize;
        this.categoryName = categoryName;
    }

    public static List<ItemResponseDto> from(List<Item> items) {
        return items.stream()
            .map(ItemResponseDto::from)
            .collect(Collectors.toUnmodifiableList());
    }

    public static ItemResponseDto from(Item item) {
        return ItemResponseDto.builder()
            .itemId(item.getId())
            .thumbnail(item.getThumbnailUrl())
            .productName(item.getProductName())
            .realPrice(item.getRealPrice())
            .discountRate(item.getDiscountRate())
            .discountPrice(item.getDiscountPrice())
            .starAvg(item.getStatistics().getStarAvg())
            .categoryName(
                item.getItemCategories().stream()
                    .map(itemCategory -> itemCategory.getCategory().getCategoryName())
                    .findFirst().orElse(null)
            )
            .build();
    }

    public static ItemResponseDto from(ItemQueryDto itemQueryDto) {
        return ItemResponseDto.builder()
            .itemId(itemQueryDto.getItemId())
            .thumbnail(itemQueryDto.getThumbnail())
            .productName(itemQueryDto.getProductName())
            .baseStandard(itemQueryDto.getBaseStandard())
            .mainFunction(itemQueryDto.getMainFunction())
            .realPrice(itemQueryDto.getRealPrice())
            .discountRate(itemQueryDto.getDiscountRate())
            .discountPrice(itemQueryDto.getDiscountPrice())
            .starAvg(itemQueryDto.getStarAvg())
            .reviewSize(itemQueryDto.getReviewSize())
            .categoryName(itemQueryDto.getCategoryName())
            .build();
    }
}
