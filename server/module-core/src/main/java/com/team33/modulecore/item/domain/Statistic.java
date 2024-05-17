package com.team33.modulecore.item.domain;

import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import javax.persistence.Embeddable;
import javax.persistence.Transient;

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

	@Transient
	private ReadWriteLock lock = new ReentrantReadWriteLock();

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
		lock.writeLock().lock();
		try {
			this.starAvg = (this.reviewCount * this.starAvg + star) / ++this.reviewCount;
		} finally {
			lock.writeLock().unlock();
		}
	}

	private void calculateAvgToSubtract(double star) {
		lock.writeLock().lock();
		try {
			this.starAvg = (this.reviewCount * this.starAvg - star) / --this.reviewCount;
		} finally {
			lock.writeLock().unlock();
		}
	}
}
