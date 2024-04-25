package com.team33.modulecore.item.domain;

import com.team33.modulecore.category.domain.Category;
import com.team33.modulecore.item.dto.ItemPostServiceDto;
import com.team33.modulecore.review.domain.Review;
import com.team33.modulecore.talk.domain.Talk;
import com.team33.modulecore.wish.domain.Wish;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Column;
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

@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class Item {

    @Id
    @Column(name = "item_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;

    private String content;

    private String thumbnail;

    private String descriptionImage;

    private String expiration;

    private int discountPrice;

    private int price;

    private int discountRate;

    private int view;

    private int sales;

    private int capacity;

    private int servingSize;

    private int totalWishes;

    private double starAvg;

    @Enumerated(value = EnumType.STRING)
    private Brand brand;

    @OneToMany(mappedBy = "item", cascade = CascadeType.ALL)
    private List<Wish> wishList = new ArrayList<>();


    @OneToMany(mappedBy = "item")
    private List<Category> categories = new ArrayList<>();


    @OneToMany(mappedBy = "item", cascade = {CascadeType.PERSIST, CascadeType.REMOVE})
    private List<Review> reviews = new ArrayList<>();


    @OneToMany(mappedBy = "item", cascade = {CascadeType.PERSIST, CascadeType.REMOVE})
    private List<Talk> talks = new ArrayList<>();


    @OneToMany(mappedBy = "item", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<NutritionFact> nutritionFacts = new ArrayList<>();

    @Builder
    private Item(
        String title,
        String content,
        String thumbnail,
        String descriptionImage,
        String expiration,
        int discountPrice,
        int price,
        int discountRate,
        int view,
        int sales,
        int capacity,
        int servingSize,
        int totalWishes,
        double starAvg,
        Brand brand,
        List<Wish> wishList,
        List<Category> categories,
        List<Review> reviews,
        List<Talk> talks,
        List<NutritionFact> nutritionFacts
    ) {
        this.title = title;
        this.content = content;
        this.thumbnail = thumbnail;
        this.descriptionImage = descriptionImage;
        this.expiration = expiration;
        this.discountPrice = discountPrice;
        this.price = price;
        this.discountRate = discountRate;
        this.view = view;
        this.sales = sales;
        this.capacity = capacity;
        this.servingSize = servingSize;
        this.totalWishes = totalWishes;
        this.starAvg = starAvg;
        this.brand = brand;
        this.wishList = wishList;
        this.categories = categories;
        this.reviews = reviews;
        this.talks = talks;
        this.nutritionFacts = nutritionFacts;
    }

    public static Item create(
        ItemPostServiceDto dto,
        List<NutritionFact> nutritionFacts,
        List<Category> category
    ) {
        Item item = Item.builder()
            .title(dto.getTitle())
            .content(dto.getContent())
            .thumbnail(dto.getThumbnail())
            .descriptionImage(dto.getDescriptionImage())
            .expiration(dto.getExpiration())
            .discountPrice(dto.getDiscountPrice())
            .price(dto.getPrice())
            .discountRate(dto.getDiscountRate())
            .capacity(dto.getCapacity())
            .servingSize(dto.getServingSize())
            .sales(dto.getSales())
            .brand(dto.getBrand())
            .starAvg(dto.getStarAvg())
            .categories(category)
            .reviews(new ArrayList<>())
            .talks(new ArrayList<>())
            .nutritionFacts(nutritionFacts)
            .wishList(new ArrayList<>())
            .build();
        //TODO: 나머지 연관관계는 리팩토링 하면서
        item.getCategories().forEach(c -> c.addItem(item));
        item.getNutritionFacts().forEach(nutritionFact -> nutritionFact.addItem(item));
        return item;
    }


    public void addCategories(Category category) {
        categories.add(category);
        category.setItem(this);
    }

    public void plusSales(int sales) {
        this.sales = sales;
    }

    public void minusSales(int quantity) {
        this.sales -= quantity;
    }
}
