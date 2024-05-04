package com.team33.modulecore.item.domain;


import javax.persistence.Embeddable;
import javax.persistence.Embedded;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@EqualsAndHashCode
@ToString
@NoArgsConstructor
@Getter
@Embeddable
public class Information {

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

    @Embedded
    private Price price;

    @Embedded
    private Image image;

    @Builder
    public Information(
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
        Price price,
        Image image
    ) {
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
        this.price = price;
        this.image = image;
    }
}
