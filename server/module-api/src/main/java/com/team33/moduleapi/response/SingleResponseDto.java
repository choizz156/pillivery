package com.team33.moduleapi.response;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import lombok.Getter;

@Getter
public class SingleResponseDto<T> {
    private final T data;
    private final ZonedDateTime createTime = ZonedDateTime.now(ZoneId.of("Asia/Seoul"));

    public SingleResponseDto(T data) {
        this.data = data;
    }
}
