package com.team33.moduleapi.response;

import java.time.ZoneId;
import java.time.ZonedDateTime;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;

@Getter
public class SingleResponseDto<T> {
    private final T data;
    @JsonProperty("createTime")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy‑MM‑dd'T'HH:mm:ssXXX")
    private final ZonedDateTime createTime = ZonedDateTime.now(ZoneId.of("Asia/Seoul"));

    public SingleResponseDto(T data) {
        this.data = data;
    }
}
