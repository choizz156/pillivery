package com.team33.modulecore.item.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;

import com.navercorp.fixturemonkey.FixtureMonkey;
import com.navercorp.fixturemonkey.api.introspector.FieldReflectionArbitraryIntrospector;
import com.team33.modulecore.EnableItemDomainTest;
import com.team33.modulecore.category.domain.Category;
import com.team33.modulecore.category.domain.CategoryName;
import com.team33.modulecore.item.domain.Item;
import com.team33.modulecore.item.dto.ItemPostServiceDto;
import com.team33.modulecore.item.dto.NutritionFactPostDto;
import java.util.Set;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.Commit;

@EnableItemDomainTest
class ItemServiceTest {

    final FixtureMonkey fixtureMonkey = FixtureMonkey
        .builder()
        .objectIntrospector(FieldReflectionArbitraryIntrospector.INSTANCE)
        .defaultNotNull(true)
        .build();

    @Autowired
    private ItemService itemService;

    @Commit
    @DisplayName("item을 저장할 수 있다.")
    @Test
    void 아이템_생성() throws Exception {
        //given
        ItemPostServiceDto sample = fixtureMonkey.giveMeBuilder(ItemPostServiceDto.class)
            .set("categories", Set.of(CategoryName.EYE, CategoryName.BONE))
            .set("nutritionFacts", Set.of(new NutritionFactPostDto("test1", "test11")))
            .sample();

        //when
        Item item = itemService.createItem(sample);
        Item item1 = itemService.createItem(sample);
        //then
        assertThat(item.getCategories()).hasSize(2)
            .extracting("categoryName")
            .containsExactlyInAnyOrder(CategoryName.EYE, CategoryName.BONE);

        assertThat(item.getNutritionFacts()).hasSize(1)
            .extracting("ingredient", "volume")
            .containsExactlyInAnyOrder(tuple("test1", "test11"));

        Item optionalItem = item.getCategories().stream().map(Category::getItem).findFirst().orElse(null);
        assertThat(optionalItem).isNotNull();
        assertThat(optionalItem.getId()).isEqualTo(item.getId());

    }

}