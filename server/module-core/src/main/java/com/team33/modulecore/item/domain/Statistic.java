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

    private int reviewCount;

    public void addView() {
        this.view ++;
    }

    public void reduceSales(int quantity) {
        this.sales -= quantity;
    }

    public void addReviewCount() {
        this.reviewCount++;
    }
}
