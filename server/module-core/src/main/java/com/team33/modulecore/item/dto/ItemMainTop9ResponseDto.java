package com.team33.modulecore.item.dto;

import com.team33.modulecore.item.domain.entity.Item;
import java.util.List;
import java.util.stream.Collectors;
import lombok.AccessLevel;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ItemMainTop9ResponseDto {

    private List<ItemResponseDto> discountRateItem;
    private List<ItemResponseDto> saleItem;

    private ItemMainTop9ResponseDto(
        List<ItemResponseDto> saleItem,
        List<ItemResponseDto> discountRateItem
    ) {
        this.saleItem = saleItem;
        this.discountRateItem = discountRateItem;
    }

    public static ItemMainTop9ResponseDto from(List<Item> sales, List<Item> discountRate) {
        List<ItemResponseDto> top9Sales = sales.stream()
            .map(ItemResponseDto::from)
            .collect(Collectors.toList());

        List<ItemResponseDto> top9discount = discountRate.stream()
            .map(ItemResponseDto::from)
            .collect(Collectors.toList());

        return new ItemMainTop9ResponseDto(top9Sales, top9discount);
    }
}
