package com.team33.modulecore.common;

import javax.validation.constraints.NotNull;
import lombok.Data;
import org.springframework.data.domain.Sort.Direction;

@Data
public class OrderPageDto {

    @NotNull
    private int page;
    @NotNull
    private int size;
    @NotNull
    private Direction sort = Direction.DESC;
}
