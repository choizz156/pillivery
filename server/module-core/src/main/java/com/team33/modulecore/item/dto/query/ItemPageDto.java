package com.team33.modulecore.item.dto.query;

import com.team33.modulecore.item.domain.ItemSortOption;
import com.team33.modulecore.item.dto.ItemPageRequestDto;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Sort.Direction;

@NoArgsConstructor
@Getter
public class ItemPageDto {

    private static final int DEFAULT_PAGE_SIZE = 16;
    private static final int MAX_SIZE = 2000;
    private static final int MIN_SIZE = 1;
    private static final ItemSortOption DEFAULT_ITEM_SORT_OPTION = com.team33.modulecore.item.domain.ItemSortOption.SALES;
    private static final Direction DEFAULT_SORT_TYPE = Direction.DESC;

    private int page;
    private int size;
    private Direction sort;
    private ItemSortOption sortOption;

    @Builder
    private ItemPageDto(int page, int size, Direction sort, ItemSortOption sortOption) {
        this.page = page;
        this.size = size;
        this.sort = sort;
        this.sortOption = sortOption;
    }

    public static ItemPageDto from(ItemPageRequestDto dto) {
        return ItemPageDto.builder()
            .page(Math.max(dto.getPage(), MIN_SIZE))
            .size(getSize(dto.getSize()))
            .sort(dto.getDirection() == Direction.ASC ? Direction.ASC : Direction.DESC)
            .sortOption(dto.getSortOption() == ItemSortOption.SALES ? DEFAULT_ITEM_SORT_OPTION
                : dto.getSortOption())
            .build();
    }

    public long getOffset() {
        return ((long) (this.page - 1) * this.size);
    }

    private static int getSize(int size) {
        return size < DEFAULT_PAGE_SIZE
            ? DEFAULT_PAGE_SIZE
            : Math.min(size, MAX_SIZE);
    }
}
