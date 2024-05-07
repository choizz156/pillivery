package com.team33.modulecore.item.dto.query;

import java.util.List;

import lombok.Data;

@Data
public class ItemMainTop9ResponseDto {

    private List<ItemQueryDto> discountRateItem;
    private List<ItemQueryDto> saleItem;

    public ItemMainTop9ResponseDto(
        List<ItemQueryDto> saleItem,
        List<ItemQueryDto> discountRateItem
    ) {
        this.saleItem = saleItem;
        this.discountRateItem = discountRateItem;
    }
}
