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
		this.originPrice = originPrice;
		this.discountRate = discountRate;
		
		BigDecimal rateDecimal = new BigDecimal(discountRate);
		BigDecimal zero = BigDecimal.ZERO;
		
		if(rateDecimal.compareTo(zero) == 0){
			this.discountPrice = 0;
			this.realPrice = originPrice;
			return;
		}

		BigDecimal origin = new BigDecimal(originPrice);
		BigDecimal rate = rateDecimal.divide(new BigDecimal(100), 10, RoundingMode.HALF_UP);
		BigDecimal discount = origin.multiply(rate).setScale(0, RoundingMode.HALF_UP);
		
		this.discountPrice = discount.intValue();
		this.realPrice = originPrice - discountPrice;
	}
}
