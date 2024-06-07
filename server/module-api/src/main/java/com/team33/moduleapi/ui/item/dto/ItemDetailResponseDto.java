package com.team33.moduleapi.ui.item.dto;

import com.team33.modulecore.category.domain.Categories;
import com.team33.modulecore.item.domain.entity.Item;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ItemDetailResponseDto { // 아이템 상세 조회

	private Long itemId;
	private String thumbnail;
	private String descriptionImage;
	private String enterprise;
	private String productName;
	private String statementNumber;
	private String registeredDate;
	private String distributionPeriod;
	private String sungsang; //원료 특성
	private String servingUse;
	private String preservePeriod;
	private String intake;
	private String mainFunction;
	private String baseStandard;
	private int realPrice;
	private int originPrice;
	private double discountRate;
	private int discountPrice;
	private int sales;
	private double starAvg;
	private Categories categories;

	@Builder
	private ItemDetailResponseDto(
		Long itemId,
		String thumbnail,
		String descriptionImage,
		String enterprise,
		String productName,
		String statementNumber,
		String registeredDate,
		String distributionPeriod,
		String sungsang,
		String servingUse,
		String preservePeriod,
		String intake,
		String mainFunction,
		String baseStandard,
		int realPrice,
		int originPrice,
		double discountRate,
		int discountPrice,
		int sales,
		double starAvg,
		Categories categories
	) {
		this.itemId = itemId;
		this.thumbnail = thumbnail;
		this.descriptionImage = descriptionImage;
		this.enterprise = enterprise;
		this.productName = productName;
		this.statementNumber = statementNumber;
		this.registeredDate = registeredDate;
		this.distributionPeriod = distributionPeriod;
		this.sungsang = sungsang;
		this.servingUse = servingUse;
		this.preservePeriod = preservePeriod;
		this.intake = intake;
		this.mainFunction = mainFunction;
		this.baseStandard = baseStandard;
		this.realPrice = realPrice;
		this.originPrice = originPrice;
		this.discountRate = discountRate;
		this.discountPrice = discountPrice;
		this.sales = sales;
		this.starAvg = starAvg;
		this.categories = categories;
	}

	public static ItemDetailResponseDto of(Item item) {
		return ItemDetailResponseDto.builder()
			.itemId(item.getId())
			.thumbnail(item.getThumbnailUrl())
			.descriptionImage(item.getDescriptionImage())
			.enterprise(item.getInformation().getEnterprise())
			.productName(item.getProductName())
			.statementNumber(item.getInformation().getStatementNumber())
			.registeredDate(item.getInformation().getRegisteredDate())
			.distributionPeriod(item.getInformation().getDistributionPeriod())
			.sungsang(item.getInformation().getSungsang())
			.servingUse(item.getServingUse())
			.preservePeriod(item.getInformation().getPreservePeriod())
			.intake(item.getInformation().getIntake())
			.mainFunction(item.getInformation().getMainFunction())
			.baseStandard(item.getInformation().getBaseStandard())
			.sales(item.getSales())
			.originPrice(item.getOriginPrice())
			.realPrice(item.getRealPrice())
			.discountRate(item.getDiscountRate())
			.discountPrice(item.getDiscountPrice())
			.starAvg(item.getStarAvg())
			.categories(
				item.getCategories()
			)
			.build();
	}
}
