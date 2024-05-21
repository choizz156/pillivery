package com.team33.moduleapi.ui.order.dto;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import lombok.Getter;

@Getter
public class OrderPostDto {
    @NotNull
    private long itemId;

    @Min(value = 1, message = "수량은 1개 이상 선택해주세요.")
    private int quantity;
    private int period;
    private boolean subscription;
}
