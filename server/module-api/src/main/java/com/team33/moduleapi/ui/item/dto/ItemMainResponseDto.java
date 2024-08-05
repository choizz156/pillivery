package com.team33.moduleapi.ui.item.dto;

import java.util.List;

import com.team33.modulecore.core.item.dto.query.ItemQueryDto;

import lombok.Data;

@Data
public class ItemMainResponseDto {

    private List<ItemQueryDto> discountRateItem;
    private List<ItemQueryDto> saleItem;

    public ItemMainResponseDto(
        List<ItemQueryDto> saleItem,
        List<ItemQueryDto> discountRateItem
    ) {
        this.saleItem = saleItem;
        this.discountRateItem = discountRateItem;
    }
}
