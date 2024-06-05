package com.team33.modulecore.category.domain;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum CategoryName {

    EYE("눈"),
    BONE("뼈,골다공증"),
    BRAIN("두뇌"),
    INTESTINE("배변,장,유산균"),
    LIVER("간"),
    SKIN("피부"),
    ANTIOXIDANT("항산화"),
    FATIGUE("피로,혈액순환"),
    ETC("기타");

    private final String name;

    public static Set<CategoryName> classify(String mainFunction) {

        if(mainFunction == null){
            return Set.of(ETC);
        }

        CategoryName[] values = CategoryName.values();
        Set<CategoryName> categoriesSet = Arrays.stream(values)
            .filter(value -> Arrays.stream(value.getName().split(",")).anyMatch(mainFunction::contains))
            .collect(Collectors.toSet());

        if(categoriesSet.isEmpty()){
            categoriesSet.add(ETC);
        }

        return categoriesSet;
    }
}
