package com.team33.modulecore.item.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;

import com.team33.modulecore.item.dto.NutritionFactPostDto;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class NutritionFactTest {

    @DisplayName("영양성분 객체를 생성할 수 있다.")
    @Test
    void 영양성분_객체_생성() throws Exception {
        //given
        var nutritionFact = List.of(new NutritionFactPostDto("test1", "test11"));

        //when
        List<NutritionFact> nutritionFacts = NutritionFact.of(nutritionFact);

        //then
        assertThat(nutritionFacts).hasSize(1)
            .extracting("ingredient", "volume")
            .containsExactlyInAnyOrder(tuple("test1", "test11"));
    }
}