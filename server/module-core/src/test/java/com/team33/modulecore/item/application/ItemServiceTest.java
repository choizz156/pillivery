package com.team33.modulecore.item.application;

import static com.team33.modulecore.category.domain.CategoryName.BONE;
import static com.team33.modulecore.category.domain.CategoryName.EYE;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

import com.navercorp.fixturemonkey.FixtureMonkey;
import com.navercorp.fixturemonkey.api.introspector.FieldReflectionArbitraryIntrospector;
import com.team33.modulecore.category.application.CategoryService;
import com.team33.modulecore.category.domain.Category;
import com.team33.modulecore.category.domain.CategoryName;
import com.team33.modulecore.item.domain.entity.Item;
import com.team33.modulecore.item.domain.entity.NutritionFact;
import com.team33.modulecore.item.domain.mock.FakeItemCommandRepository;
import com.team33.modulecore.item.domain.repository.ItemCommandRepository;
import com.team33.modulecore.item.dto.ItemPostServiceDto;
import com.team33.modulecore.item.dto.NutritionFactPostDto;
import java.util.List;
import java.util.Optional;
import java.util.stream.IntStream;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;


class ItemServiceTest{

    final FixtureMonkey fixtureMonkey = FixtureMonkey
        .builder()
        .objectIntrospector(FieldReflectionArbitraryIntrospector.INSTANCE)
        .defaultNotNull(true)
        .build();

    @DisplayName("item을 저장할 수 있다.")
    @Test
    void 아이템_생성() throws Exception {
        //given
        var sample = fixtureMonkey.giveMeBuilder(ItemPostServiceDto.class)
            .set("categories", List.of(CategoryName.EYE, CategoryName.BONE))
            .set("nutritionFacts", List.of(new NutritionFactPostDto("test1", "test11")))
            .set("discountRate", 1.0)
            .sample();

        var nutritionFactService = mock(NutritionFactService.class);
        var categoryService = mock(CategoryService.class);

        var nutritionFacts = NutritionFact.createList(sample.getNutritionFacts());
        given(nutritionFactService.getNutritionFacts(any())).willReturn(nutritionFacts);

        var categories = List.of(Category.of(EYE), Category.of(BONE));
        given(categoryService.getCategories(anyList())).willReturn(categories);

        var itemCommandService = new ItemService(
            null,
            nutritionFactService,
            categoryService,
            new FakeItemCommandRepository(),
            null
        );

        //when
        Item resultItem = itemCommandService.createItem(sample);

        //then
        assertThat(resultItem.getItemCategories()).hasSize(2)
            .extracting("category.categoryName", "item")
            .containsExactlyInAnyOrder(tuple(BONE, resultItem), tuple(EYE, resultItem));

        assertThat(resultItem.getNutritionFacts()).hasSize(1)
            .extracting("ingredient", "volume", "item")
            .containsExactlyInAnyOrder(tuple("test1", "test11", resultItem));

    }

    @DisplayName("아이템을 조회하면서 view수를 늘릴 수 있다.")
    @Test
    void 아이템_조회수_증가_조회() throws Exception {
        //given
        Item item = fixtureMonkey.giveMeBuilder(Item.class)
            .set("id", 1L)
            .set("view", 0L)
            .sample();

        ItemCommandRepository itemRepository = mock(ItemCommandRepository.class);
        given(itemRepository.findById(anyLong())).willReturn(Optional.ofNullable(item));

        var itemCommandService = new ItemService(
            null,
            null,
            null,
            itemRepository,
            null
        );

        //when
        IntStream.range(0, 99).forEach(
            i -> itemCommandService.findItemWithAddingView(1L)
        );
        Item result = itemCommandService.findItemWithAddingView(1L);
        //then
        assertThat(result.getView()).isEqualTo(100);
    }
}