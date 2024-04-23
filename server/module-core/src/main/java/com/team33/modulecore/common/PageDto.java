package com.team33.modulecore.common;

import lombok.Data;
import org.springframework.data.domain.Sort.Direction;

@Data
public class PageDto {

    private int page;
    private int size;
    private Direction sort = Direction.DESC;
}
