package com.team33.modulecore.order.dto;

import org.springframework.data.domain.Sort.Direction;

import lombok.Builder;
import lombok.Getter;

@Getter
public class OrderPageRequest {

    private static final int DEFAULT_PAGE_SIZE = 7;
    private static final int MAX_SIZE = 2000;
    private static final int MIN_SIZE = 1;
    private static final Direction DEFAULT_SORT_TYPE = Direction.DESC;

    private int page = MIN_SIZE;
    private int size = DEFAULT_PAGE_SIZE;
    private Direction sort = DEFAULT_SORT_TYPE;

    @Builder
    private OrderPageRequest(int page, int size, Direction sort) {
        this.page = page;
        this.size = size;
        this.sort = sort;
    }

    public static OrderPageRequest of(OrderPage dto) {
        return OrderPageRequest.builder()
            .page(Math.max(dto.getPage(), MIN_SIZE))
            .size(getSize(dto.getSize()))
            .sort(dto.getSort() == Direction.ASC ? Direction.ASC : Direction.DESC)
            .build();
    }

    public static OrderPageRequest of(int page, int size, Direction sort) {
        return OrderPageRequest.builder()
            .page(Math.max(page, MIN_SIZE))
            .size(getSize(size))
            .sort(sort == Direction.ASC ? Direction.ASC : Direction.DESC)
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
