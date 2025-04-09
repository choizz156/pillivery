package com.team33.modulecore.core.item.domain;

import java.math.BigDecimal;
import java.math.RoundingMode;

import javax.persistence.Embeddable;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;


@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Embeddable
public class Price {

	private int originPrice;   
	private int realPrice;     
	private int discountPrice; 
	private Double discountRate; 


	@Builder
	public Price(int originPrice, double discountRate) {
		validateOriginPrice(originPrice);
		this.originPrice = originPrice;

		BigDecimal normalizedRate = normalizeDiscountRate(discountRate);
		this.discountRate = normalizedRate.doubleValue();

		this.discountPrice = calculateDiscountAmount(originPrice, normalizedRate);
		this.realPrice = originPrice - this.discountPrice;
	}
	
	public int getDiscountedPrice() {
		return this.realPrice;
	}
	
	public double getDiscountRatePercent() {
		return this.discountRate * 100;
	}
	
	private void validateOriginPrice(int originPrice) {
		if (originPrice < 0) {
			throw new IllegalArgumentException("원가는 음수가 될 수 없습니다: " + originPrice);
		}
	}
	
	private BigDecimal normalizeDiscountRate(double rate) {

		if (rate < 0) {
			return BigDecimal.ZERO;
		}

		BigDecimal rateDecimal = new BigDecimal(String.valueOf(rate)).setScale(1, RoundingMode.HALF_UP);

		if (rateDecimal.compareTo(BigDecimal.ZERO) == 0) {
			return BigDecimal.ZERO;
		}

		return rateDecimal.divide(new BigDecimal("100"), 10, RoundingMode.HALF_UP);
	}
	
	private int calculateDiscountAmount(int price, BigDecimal rate) {

		if (rate.compareTo(BigDecimal.ZERO) == 0) {
			return 0;
		}

		BigDecimal priceDecimal = new BigDecimal(price);
		return priceDecimal.multiply(rate)
				.setScale(0, RoundingMode.HALF_UP)
				.intValue();
	}
}
