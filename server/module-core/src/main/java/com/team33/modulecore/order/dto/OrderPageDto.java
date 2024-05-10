package com.team33.modulecore.order.dto;

import javax.validation.constraints.NotNull;

import org.springframework.data.domain.Sort.Direction;

import lombok.Data;

@Data
public class OrderPageDto {

    @NotNull
    private int page;
    @NotNull
    private int size;
    @NotNull
    private Direction sort = Direction.DESC;
}
