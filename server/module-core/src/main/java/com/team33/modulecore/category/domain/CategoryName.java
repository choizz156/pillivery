package com.team33.modulecore.category.domain;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

import com.team33.modulecore.item.domain.entity.Item;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum CategoryName {

    EYE("눈"),
    BONE("뼈"),
    BRAIN("두뇌"),
    INTESTINE("배변"),
    LIVER("간"),
    SKIN("피부"),
    ANTIOXIDANT("항산화"),
    FATIGUE("피로"),
    ETC("기타");

    private final String name;

    public static Set<CategoryName> classify(Item item) {
        CategoryName[] values = CategoryName.values();
        Set<CategoryName> categoriesSet = Arrays.stream(values)
            .filter(value -> item.getInformation().getMainFunction().contains(value.getName()))
            .collect(Collectors.toSet());

        if(categoriesSet.isEmpty()){
            categoriesSet.add(ETC);
        }

        return categoriesSet;
    }
}
