package com.team33.modulecore.item.dto;

import com.team33.modulecore.item.domain.ItemSortOption;
import javax.validation.constraints.NotNull;
import lombok.Data;
import org.springframework.data.domain.Sort.Direction;

@Data
public class ItemPageDto {

    @NotNull
    private int page;
    @NotNull
    private int size;
    @NotNull
    private ItemSortOption sortOption = ItemSortOption.SALES;
    @NotNull
    private Direction direction = Direction.DESC;
}
