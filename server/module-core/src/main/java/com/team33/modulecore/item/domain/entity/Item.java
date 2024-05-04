package com.team33.modulecore.item.domain.entity;

import com.team33.modulecore.category.domain.ItemCategory;
import com.team33.modulecore.common.BaseEntity;
import com.team33.modulecore.item.domain.Information;
import com.team33.modulecore.item.domain.Price;
import com.team33.modulecore.item.domain.Statistic;
import java.util.HashSet;
import java.util.Set;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.DynamicUpdate;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@DynamicUpdate
@Entity
public class Item extends BaseEntity {

    @Id
    @Column(name = "item_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Embedded
    private Information information;

    @Embedded
    private Statistic statistics;

    //TODO: 이것도 정보안에 넣는다.


    @OneToMany(mappedBy = "item")
    private Set<ItemCategory> itemCategories = new HashSet<>();

//    @OneToMany(mappedBy = "item", cascade = CascadeType.ALL, orphanRemoval = true)
//    private List<Review> reviews = new ArrayList<>();

    @Builder
    private Item(
        Information information,
        Price price,
        Statistic statistics,
        Set<ItemCategory> itemCategories
    ) {
        this.information = information;
        this.statistics = statistics;
        this.itemCategories = itemCategories;
    }

    public static Item create(Information information) {
        return Item.builder()
            .information(information)
            .statistics(new Statistic())
            .build();
    }

    public void addView() {
       this.statistics.addView();
    }

    public String getThumbnailUrl() {
        return this.information.getImage().getThumbnail();
    }

    public int getOriginPrice(){
        return this.information.getPrice().getOriginPrice();
    }

    public double getDiscountRate(){
        return this.information.getPrice().getDiscountRate();
    }
    public String getProductName(){
        return this.information.getProductName();
    }
    public int getDiscountPrice(){
        return this.information.getPrice().getDiscountPrice();
    }

    public int getRealPrice() {
        return this.information.getPrice().getRealPrice();
    }

    public void minusSales(int quantity) {
        this.statistics.reduceSales(quantity);
    }

    public String getDescriptionImage() {
        return this.getInformation().getImage().getDescriptionImage();
    }

    public int getSales() {
        return this.statistics.getSales();
    }

    public String getServingUse() {
        return this.information.getServingUse();
    }

    public double getStarAvg() {
        return this.statistics.getStarAvg();
    }
}
