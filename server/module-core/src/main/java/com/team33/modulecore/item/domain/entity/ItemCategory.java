package com.team33.modulecore.item.domain.entity;

import com.team33.modulecore.category.domain.Category;
import com.team33.modulecore.common.BaseEntity;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "item_category")
public class ItemCategory extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "category_id")
    private Category category;

    @ManyToOne
    @JoinColumn(name = "item_id")
    private Item item;


    private ItemCategory(Category category, Item item) {
        this.category = category;
        this.item = item;
    }

    public static ItemCategory of(Item item, Category c) {
       return new ItemCategory(null, null);
    }

    public static ItemCategory of(Category category) {
        ItemCategory itemCategory = new ItemCategory();
        itemCategory.addCategory(category);
        return itemCategory;
    }

    public void addItem(Item item) {
        this.item = item;
        this.item.getItemCategories().add(this);
    }

    void addCategory(Category category) {
        this.category = category;
        this.category.getItemCategories().add(this);
    }
}