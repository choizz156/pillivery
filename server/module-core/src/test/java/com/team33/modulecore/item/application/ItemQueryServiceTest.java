package com.team33.modulecore.item.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

import com.navercorp.fixturemonkey.FixtureMonkey;
import com.navercorp.fixturemonkey.api.introspector.FieldReflectionArbitraryIntrospector;
import com.navercorp.fixturemonkey.javax.validation.plugin.JavaxValidationPlugin;
import com.team33.modulecore.category.domain.CategoryName;
import com.team33.modulecore.item.domain.Brand;
import com.team33.modulecore.item.domain.repository.ItemQueryRepository;
import com.team33.modulecore.item.dto.ItemResponseDto;
import com.team33.modulecore.item.dto.ItemSearchRequest;
import com.team33.modulecore.item.dto.PriceFilterDto;
import com.team33.modulecore.item.dto.query.ItemQueryDto;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

class ItemQueryServiceTest {


    @DisplayName("가격에 따라 필터링된 아이템을 조회할 수 있다.")
    @Test
    void 가격에_따른_조회() throws Exception {
        //given
        FixtureMonkey fixtureMonkey = FixtureMonkey.builder()
            .defaultNotNull(true)
            .objectIntrospector(FieldReflectionArbitraryIntrospector.INSTANCE)
            .plugin(new JavaxValidationPlugin())
            .build();

        ItemQueryDto sample = fixtureMonkey.giveMeBuilder(ItemQueryDto.class)
            .set("title", "title")
            .set("brand", Brand.MYNI)
            .set("nutritionFacts", new ArrayList<>())
            .set("categoryNames", List.of(CategoryName.EYE))
            .sample();

//        MockEntityFactory mockEntityFactory = MockEntityFactory.of();
//        Item mockItem = mockEntityFactory.getMockItem();

        ItemQueryRepository itemQueryRepository = mock(ItemQueryRepository.class);

        given(itemQueryRepository.findItemsByPrice(
                any(PriceFilterDto.class),
                any(ItemSearchRequest.class)
            )
        ).willReturn(
            new PageImpl<>(List.of(sample), PageRequest.of(1, 1), 1)
        );

        ItemQueryService itemQueryService = new ItemQueryService(itemQueryRepository);

        //when
        Page<ItemResponseDto> filteredItemByPrice =
            itemQueryService.findFilteredItemByPrice(new PriceFilterDto(), new ItemSearchRequest());

        //then
        List<ItemResponseDto> content = filteredItemByPrice.getContent();
        assertThat(content).hasSize(1)
            .extracting("title", "brand")
            .containsExactlyInAnyOrder(tuple("title", Brand.MYNI));
    }

}