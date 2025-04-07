package com.team33.modulecore.core.item.domain;

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

	public void addStarAvg(double star) {
		if (isFirst(star))
			return;

		calculateAvgToAdd(star);
	}

	public void subtractStarAvg(double star) {
		if (isOneReviewOnly())
			return;

		calculateAvgToSubtract(star);
	}

	private boolean isFirst(double star) {
		if (reviewCount == 0) {
			this.starAvg = star;
			this.reviewCount = 1;
			return true;
		}
		return false;
	}

	private boolean isOneReviewOnly() {
		if (this.reviewCount == 1) {
			this.starAvg = 0.0;
			this.reviewCount = 0;
			return true;
		}
		return false;
	}

	private void calculateAvgToAdd(double star) {
			this.starAvg = (this.reviewCount * this.starAvg + star) / ++this.reviewCount;
	}

	private void calculateAvgToSubtract(double star) {
			this.starAvg = (this.reviewCount * this.starAvg - star) / --this.reviewCount;
	}
}
