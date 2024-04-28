package com.team33.modulecore.category.domain;

import com.team33.modulecore.common.BaseEntity;
import com.team33.modulecore.item.domain.entity.ItemCategory;
import java.util.HashSet;
import java.util.Set;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.DynamicUpdate;


@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@DynamicUpdate
@Entity
public class Category extends BaseEntity {

    @Id
    @Column(name = "category_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    @Enumerated(EnumType.STRING)
    private CategoryName categoryName;

    @OneToMany(mappedBy = "category")
    private Set<ItemCategory> itemCategories = new HashSet<>();

    private Category(CategoryName categoryName) {
        this.categoryName = categoryName;
    }

    public static Category of(CategoryName name) {
        if(name == null) {
            throw new IllegalArgumentException("카테고리는 null일 수 없습니다.");
        }
        return new Category(name);
    }
}
