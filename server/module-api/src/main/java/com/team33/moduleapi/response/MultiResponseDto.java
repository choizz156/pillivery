package com.team33.moduleapi.response;

import java.time.ZonedDateTime;
import java.util.List;

import org.springframework.data.domain.Page;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MultiResponseDto<T> {

    private ZonedDateTime time = ZonedDateTime.now();
    private List<T> data;
    private PageInfo pageInfo;

    public MultiResponseDto(List<T> data, Page<?> page) {
        this.data = data;
        this.pageInfo = new PageInfo(
            page.getNumber() + 1,
            page.getSize(),
            page.getTotalElements(),
            page.getTotalPages()
        );
    }

    public MultiResponseDto(List<T> data) {
        this.data = data;
    }
}
