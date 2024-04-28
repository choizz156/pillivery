package com.team33.modulecore.item.domain.entity;

import com.team33.modulecore.common.BaseEntity;
import com.team33.modulecore.item.domain.Brand;
import com.team33.modulecore.item.domain.ItemPrice;
import com.team33.modulecore.item.dto.ItemPostServiceDto;
import com.team33.modulecore.review.domain.Review;
import com.team33.modulecore.wish.domain.Wish;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.DynamicUpdate;

@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@DynamicUpdate
@Entity
public class Item extends BaseEntity {

    @Id
    @Column(name = "item_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;

    private String content;

    private String thumbnail;

    private String descriptionImage;

    private String expiration;

    @Embedded
    private ItemPrice itemPrice;

    private long view;

    private int sales;

    private int capacity;

    private int servingSize;

    private int totalWishes;

    private double starAvg;

    @Enumerated(value = EnumType.STRING)
    private Brand brand;

    @OneToMany(mappedBy = "item")
    private Set<ItemCategory> itemCategories = new HashSet<>();

    @OneToMany(mappedBy = "item", cascade = CascadeType.ALL)
    private List<Wish> wishList = new ArrayList<>();

    @OneToMany(mappedBy = "item", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Review> reviews = new ArrayList<>();

    @OneToMany(mappedBy = "item", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<NutritionFact> nutritionFacts = new ArrayList<>();

    @Builder
    private Item(
        String title,
        String content,
        String thumbnail,
        String descriptionImage,
        String expiration,
        ItemPrice itemPrice,
        int view,
        int sales,
        int capacity,
        int servingSize,
        int totalWishes,
        double starAvg,
        Brand brand,
        List<Wish> wishList,
        Set<ItemCategory> itemCategories,
        List<Review> reviews,
        List<NutritionFact> nutritionFacts
    ) {
        this.title = title;
        this.content = content;
        this.thumbnail = thumbnail;
        this.descriptionImage = descriptionImage;
        this.expiration = expiration;
        this.itemPrice = itemPrice;
        this.view = view;
        this.sales = sales;
        this.capacity = capacity;
        this.servingSize = servingSize;
        this.totalWishes = totalWishes;
        this.starAvg = starAvg;
        this.brand = brand;
        this.wishList = wishList;
        this.itemCategories = itemCategories;
        this.reviews = reviews;
        this.nutritionFacts = nutritionFacts;
    }

    public static Item create(
        ItemPostServiceDto dto,
        List<NutritionFact> nutritionFacts,
        Set<ItemCategory> itemCategories
    ) {
        Item item = Item.builder()
            .title(dto.getTitle())
            .content(dto.getContent())
            .thumbnail(dto.getThumbnail())
            .descriptionImage(dto.getDescriptionImage())
            .expiration(dto.getExpiration())
            .itemPrice(new ItemPrice(dto.getPrice(), dto.getDiscountRate()))
            .capacity(dto.getCapacity())
            .servingSize(dto.getServingSize())
            .sales(dto.getSales())
            .brand(dto.getBrand())
            .starAvg(dto.getStarAvg())
            .reviews(new ArrayList<>())
            .itemCategories(itemCategories)
            .nutritionFacts(nutritionFacts)
            .wishList(new ArrayList<>())
            .build();

        item.getItemCategories().forEach(itemCategory -> itemCategory.addItem(item));
        item.getNutritionFacts().forEach(nutritionFact -> nutritionFact.addItem(item));
        //TODO: 나머지 연관관계는 리팩토링 하면서
        return item;
    }

    public void plusSales(int sales) {
        this.sales = sales;
    }

    public void minusSales(int quantity) {
        this.sales -= quantity;
    }

    public void addView() {
        this.view += 1L;
    }

    public List<Review> getItemReviewsBy5() {
        return this.reviews.stream()
            .limit(5)
            .collect(Collectors.toUnmodifiableList());
    }

    public int getDiscountPrice() {
        return this.itemPrice.getDiscountPrice();
    }

    public int getOriginalPrice() {
        return this.itemPrice.getOriginPrice();
    }

    public double getDiscountRate() {
        return this.itemPrice.getDiscountRate();
    }

    public int getRealPrice() {
        return this.itemPrice.getRealPrice();
    }
}
