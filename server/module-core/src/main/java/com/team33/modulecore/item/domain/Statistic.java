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
		lock.writeLock().lock();
		try {
			this.starAvg = (this.reviewCount * this.starAvg + star) / reviewCount + 1;
			this.reviewCount++;
		} finally {
			lock.writeLock().unlock();
		}
	}

	public void subtractStarAvg(double star) {
		lock.writeLock().lock();
		try {
			this.starAvg = (this.reviewCount * this.starAvg - star) / reviewCount - 1;
			this.reviewCount--;
		} finally {
			lock.writeLock().unlock();
		}
	}
}
