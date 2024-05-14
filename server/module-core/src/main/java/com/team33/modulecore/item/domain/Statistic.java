package com.team33.modulecore.item.domain;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

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

    private double starAvg;

    private int reviewCount;

   public void calculateStarAvg(double star) {
       AtomicReference<Double> starAvg = new AtomicReference<>(this.starAvg);
       AtomicInteger reviewCount = new AtomicInteger(this.reviewCount);

       this.reviewCount = reviewCount.incrementAndGet();
       this.starAvg = starAvg.updateAndGet(v -> (v * this.reviewCount + star) / reviewCount.get());
   }

    public void subtractReviewCount() {
        AtomicInteger reviewCount = new AtomicInteger(this.reviewCount);
        this.reviewCount = reviewCount.decrementAndGet();
    }
}
