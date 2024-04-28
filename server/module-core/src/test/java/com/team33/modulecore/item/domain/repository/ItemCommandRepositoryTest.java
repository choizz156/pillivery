package com.team33.modulecore.item.domain.repository;

import static org.assertj.core.api.Assertions.assertThat;

import com.navercorp.fixturemonkey.FixtureMonkey;
import com.navercorp.fixturemonkey.api.introspector.FieldReflectionArbitraryIntrospector;
import com.team33.modulecore.RepositorySetting;
import com.team33.modulecore.item.domain.Brand;
import com.team33.modulecore.item.domain.entity.Item;
import com.team33.modulecore.item.domain.mock.TestItemCommandRepository;
import java.util.ArrayList;
import java.util.HashSet;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * {@linkplain TestItemCommandRepository}는 테스트용 가짜 레포지토리입니다.
 */
public class ItemCommandRepositoryTest extends RepositorySetting {

    private final static ItemCommandRepository itemCommandRepository =
        new TestItemCommandRepository(getEmAtSuperClass());


    private static FixtureMonkey FIXTURE_MONKEY = FixtureMonkey
        .builder()
        .objectIntrospector(FieldReflectionArbitraryIntrospector.INSTANCE)
        .defaultNotNull(true)
        .build();


    @DisplayName("item을 저장할 수 있다.")
    @Test
    void 아이템_저장() throws Exception {
        //given
        Item item = FIXTURE_MONKEY.giveMeBuilder(Item.class).set("id", null)
            .set("nutritionFacts", new ArrayList<>())
            .set("reviews", null)
            .set("brand", Brand.MYNI)
            .set("wishList", null)
            .set("itemCategories", new HashSet<>())
            .sample();

        //when
        Item save = itemCommandRepository.save(item);

        //then
        assertThat(save.getId()).isNotNull();
    }

    @DisplayName("item을 id로 조회할 수 있다.")
    @Test
    void 아이템_조회() throws Exception {
        //given
        Item item = FIXTURE_MONKEY.giveMeBuilder(Item.class).set("id", null)
            .set("nutritionFacts", new ArrayList<>())
            .set("reviews", null)
            .set("brand", Brand.MYNI)
            .set("wishList", null)
            .set("itemCategories", new HashSet<>())
            .sample();

        Item save = itemCommandRepository.save(item);

        //when
        Item result = itemCommandRepository.findById(save.getId()).get();

        //then
        assertThat(result).isSameAs(item);
    }
}
