package com.team33.moduleapi.ui.item.dto;

import javax.validation.constraints.NotNull;

import org.springframework.data.domain.Sort.Direction;

import com.team33.modulecore.item.domain.ItemSortOption;

import lombok.Data;

@Data
public class ItemPageRequestDto {

    @NotNull
    private int page;
    @NotNull
    private int size;
    @NotNull
    private ItemSortOption sortOption = ItemSortOption.SALES;
    @NotNull
    private Direction direction = Direction.DESC;
}
