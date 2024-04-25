package com.team33.modulecore.item.domain;

import com.team33.modulecore.category.domain.Category;
import com.team33.modulecore.nutritionFact.domain.NutritionFact;
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
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class Item {

    @Id
    @Column(name = "item_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long itemId;

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


    @OneToMany(mappedBy = "item", cascade = CascadeType.ALL)
    private List<Category> categories = new ArrayList<>();


    @OneToMany(mappedBy = "item", cascade = {CascadeType.PERSIST, CascadeType.REMOVE})
    private List<Review> reviews = new ArrayList<>();


    @OneToMany(mappedBy = "item", cascade = {CascadeType.PERSIST, CascadeType.REMOVE})
    private List<Talk> talks = new ArrayList<>();


    @OneToMany(mappedBy = "item", cascade = {CascadeType.PERSIST, CascadeType.REMOVE})
    private List<NutritionFact> nutritionFacts = new ArrayList<>();


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
