package com.team33.modulecore.core.order.dto;

import javax.validation.constraints.NotNull;

import org.springframework.data.domain.Sort.Direction;

import lombok.Data;

@Data
public class OrderPage {

    @NotNull
    private int page;
    @NotNull
    private int size;
    @NotNull
    private Direction sort = Direction.DESC;
}
