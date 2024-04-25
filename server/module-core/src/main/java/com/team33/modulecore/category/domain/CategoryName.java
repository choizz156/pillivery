package com.team33.modulecore.category.domain;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum CategoryName {

    EYE("눈"),
    BONE("뼈/관절"),
    BRAIN("뇌"),
    INTESTINE("장"),
    LIVER("간"),
    SKIN("피부"),
    ANTIOXIDANT("항산화"),
    FATIGUE("피로"),
    ETC("기타");

    private final String name;
}
