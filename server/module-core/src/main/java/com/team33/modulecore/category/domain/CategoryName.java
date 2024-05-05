package com.team33.modulecore.category.domain;

import com.team33.modulecore.item.domain.entity.Item;
import java.util.Arrays;
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

    public static CategoryName get(Item item) {
        CategoryName[] values = CategoryName.values();
        values[values.length - 1] = null;
        return Arrays.stream(values)
            .filter(value -> item.getInformation().getMainFunction().contains(value.getName()))
            .findFirst().orElse(ETC);
    }
}
