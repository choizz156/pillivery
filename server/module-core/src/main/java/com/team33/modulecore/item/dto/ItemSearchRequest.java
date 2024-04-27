package com.team33.modulecore.item.dto;

import lombok.Builder;
import lombok.Getter;
import org.springframework.data.domain.Sort.Direction;

@Getter
public class ItemSearchRequest {

    private static final int DEFAULT_PAGE_SIZE = 16;
    private static final int MAX_SIZE = 2000;
    private static final int MIN_SIZE = 1;
    private static final ItemSortOption DEFAULT_ITEM_SORT_OPTION = ItemSortOption.SALES;
    private static final Direction DEFAULT_SORT_TYPE = Direction.DESC;

    private int page = MIN_SIZE;
    private int size = DEFAULT_PAGE_SIZE;
    private Direction sort = DEFAULT_SORT_TYPE;
    private ItemSortOption sortOption = DEFAULT_ITEM_SORT_OPTION;

    @Builder
    private ItemSearchRequest(int page, int size, Direction sort, ItemSortOption sortOption) {
        this.page = page;
        this.size = size;
        this.sort = sort;
        this.sortOption = sortOption;
    }

    public static ItemSearchRequest to(ItemPageDto dto) {
        return ItemSearchRequest.builder()
            .page(Math.max(dto.getPage(), MIN_SIZE))
            .size(getSize(dto.getSize()))
            .sort(dto.getDirection() == Direction.ASC ? Direction.ASC : Direction.DESC)
            .sortOption(dto.getSortOption())
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
