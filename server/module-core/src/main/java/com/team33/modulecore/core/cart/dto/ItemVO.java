package com.team33.modulecore.core.cart.dto;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

@EqualsAndHashCode
@NoArgsConstructor
@Getter
public class ItemVO {
	private Long id;
	private String enterprise;
	private String thumbnailUrl;
	private String productName;
	private int originPrice;
	private int discountPrice;
	private double discountRate;
	private int realPrice;

	@Builder
	public ItemVO(
		Long id,
		String enterprise,
		String thumbnailUrl,
		String productName,
		int originPrice,
		int discountPrice,
		double discountRate,
		int realPrice
	) {
		this.id = id;
		this.enterprise = enterprise;
		this.thumbnailUrl = thumbnailUrl;
		this.productName = productName;
		this.originPrice = originPrice;
		this.discountPrice = discountPrice;
		this.discountRate = discountRate;
		this.realPrice = realPrice;
	}
}
