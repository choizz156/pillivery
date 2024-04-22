package com.team33.modulecore.order.dto;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Sort.Direction;

@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class OrderPageRequest {

    private static final int DEFAULT_PAGE_SIZE = 7;
    private static final int MAX_SIZE = 2000;
    private static final int MIN_SIZE = 1;
    private static final Direction DEFAULT_SORT_TYPE = Direction.DESC;

    private int page;
    private int size;
    private Direction sort;

    private OrderPageRequest(int size, int page) {
        this(size, page, DEFAULT_SORT_TYPE);
    }

    private OrderPageRequest(int page, int size, Direction sort) {
        this.page = Math.max(page, MIN_SIZE);
        this.size = getSize(size);
        this.sort = (sort == Direction.ASC ? Direction.ASC : Direction.DESC);
    }

    public static OrderPageRequest of(int page, int size) {
        return new OrderPageRequest(page, size);
    }

    public static OrderPageRequest of(int page, int size, Direction sort) {
        return new OrderPageRequest(page, size, sort);
    }

    public long getOffset(){
        return ((long) (this.page - 1) * this.size);
    }

    private int getSize(int size) {
        return size < DEFAULT_PAGE_SIZE
            ? size
            : Math.min(size, MAX_SIZE);
    }
}
