package com.team33.modulecore.nutritionFact.domain;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import com.team33.modulecore.item.domain.Item;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Builder
public class NutritionFact {

    @Id
    @GeneratedValue
    private Long nutritionFactId;

    @ManyToOne
    @JoinColumn(name = "ITEM_ID")
    private Item item;

    private String ingredient;

    private String volume;
}
