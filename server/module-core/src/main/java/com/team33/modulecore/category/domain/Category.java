package com.team33.modulecore.category.domain;

import com.team33.modulecore.item.domain.entity.Item;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.DynamicUpdate;


@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@DynamicUpdate
@Entity
public class Category {

    @Id
    @Column(name = "category_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Setter
    @ManyToOne
    @JoinColumn(name = "item_id")
    private Item item;


    @Enumerated(EnumType.STRING)
    private CategoryName categoryName;

    public Category(CategoryName categoryName) {
        if(categoryName == null) {
            throw new IllegalArgumentException("카테고리는 null일 수 없습니다.");
        }

        this.categoryName = categoryName;
    }

    public void addItem(Item item) {
        this.item = item;
    }
}
