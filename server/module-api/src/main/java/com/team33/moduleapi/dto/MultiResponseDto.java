package com.team33.moduleapi.dto;

import java.time.ZonedDateTime;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Page;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MultiResponseDto<T> {

    private ZonedDateTime time = ZonedDateTime.now();
    private List<T> data;
    private PageInfo pageInfo;

    public MultiResponseDto(List<T> data, Page<T> page) {
        this.data = data;
        this.pageInfo = new PageInfo(
            page.getNumber() + 1,
            page.getSize(),
            page.getTotalElements(),
            page.getTotalPages());
    }

    public MultiResponseDto(List<T> data) {
        this.data = data;
    }
}
