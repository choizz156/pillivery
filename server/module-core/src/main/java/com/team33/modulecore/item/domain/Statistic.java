package com.team33.modulecore.item.domain;

import javax.persistence.Embeddable;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter
@EqualsAndHashCode
@ToString
@NoArgsConstructor
@Embeddable
public class Statistic {

    private long view;

    private int sales;

    private int totalWishes;

    private double starAvg;

    public void addView() {
        this.view += 1;
    }

    public void reduceSales(int quantity) {
        this.sales -= quantity;
    }
}
