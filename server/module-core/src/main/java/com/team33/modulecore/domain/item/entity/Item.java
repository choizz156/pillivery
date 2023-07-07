package com.team33.modulecore.domain.item.entity;

import com.team33.modulecore.domain.category.entity.Category;
import com.team33.modulecore.domain.nutritionFact.entity.NutritionFact;
import com.team33.modulecore.domain.review.entity.Review;
import com.team33.modulecore.domain.talk.entity.Talk;
import com.team33.modulecore.domain.wish.entity.Wish;
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
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class Item {

    @Id
    @Column(name = "ITEM_ID")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long itemId;

    @Column
    private String title;

    @Column
    private String content;

    @Column
    private String thumbnail;

    @Column
    private String descriptionImage;

    @Column
    private String expiration;

    @Column
    private int discountPrice;

    @Column
    private int price;

    @Column
    private int discountRate;

    @Column
    private int view;

    @Column
    private int sales;

    @Column
    private int capacity;


    @Column
    private int servingSize;


    @Column
    private int totalWishes;


    @Enumerated(value = EnumType.STRING)
    private Brand brand;


    @OneToMany(mappedBy = "item", cascade = CascadeType.ALL)
    private List<Wish> wishList = new ArrayList<>();


    @OneToMany(mappedBy = "item", cascade = CascadeType.ALL)
    private List<Category> categories = new ArrayList<>();



    public void addCategories(Category category) {
        categories.add(category);
        category.setItem(this);
    }


    @Column
    private double starAvg;


    @OneToMany(mappedBy = "item", cascade = {CascadeType.PERSIST, CascadeType.REMOVE})
    private List<Review> reviews = new ArrayList<>();


    @OneToMany(mappedBy = "item", cascade = {CascadeType.PERSIST, CascadeType.REMOVE})
    private List<Talk> talks = new ArrayList<>();


    @OneToMany(mappedBy = "item", cascade = {CascadeType.PERSIST, CascadeType.REMOVE})
    private List<NutritionFact> nutritionFacts = new ArrayList<>();


}
