package com.team33.modulecore.item.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;

import com.navercorp.fixturemonkey.FixtureMonkey;
import com.navercorp.fixturemonkey.api.introspector.FieldReflectionArbitraryIntrospector;
import com.team33.modulecore.EnableItemDomainTest;
import com.team33.modulecore.category.domain.Category;
import com.team33.modulecore.category.domain.CategoryName;
import com.team33.modulecore.category.repository.CategoryRepository;
import com.team33.modulecore.item.domain.Brand;
import com.team33.modulecore.item.domain.entity.Item;
import com.team33.modulecore.item.domain.repository.ItemRepository;
import com.team33.modulecore.item.dto.ItemPageDto;
import com.team33.modulecore.item.dto.ItemPostServiceDto;
import com.team33.modulecore.item.dto.ItemSearchRequest;
import com.team33.modulecore.item.dto.NutritionFactPostDto;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.IntStream;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;


@EnableItemDomainTest
class ItemServiceTest {

    final FixtureMonkey fixtureMonkey = FixtureMonkey
        .builder()
        .objectIntrospector(FieldReflectionArbitraryIntrospector.INSTANCE)
        .defaultNotNull(true)
        .build();

    @Autowired
    private CategoryRepository categoryRepository;
    @Autowired
    private ItemService itemService;
    @Autowired
    private ItemQueryService itemQueryService;
    @Autowired
    private ItemRepository itemRepository;


    @AfterEach
    void tearDown() {
        categoryRepository.deleteAllInBatch();
        itemRepository.deleteAll();
    }


    @DisplayName("item을 저장할 수 있다.")
    @Test
    void 아이템_생성() throws Exception {
        //given
        var sample = fixtureMonkey.giveMeBuilder(ItemPostServiceDto.class)
            .set("categories", List.of(CategoryName.EYE, CategoryName.BONE))
            .set("nutritionFacts", List.of(new NutritionFactPostDto("test1", "test11")))
            .sample();

        //when
        var item = itemService.createItem(sample);

        //then
        assertThat(item.getCategories()).hasSize(2)
            .extracting("categoryName")
            .containsExactlyInAnyOrder(CategoryName.EYE, CategoryName.BONE);

        assertThat(item.getNutritionFacts()).hasSize(1)
            .extracting("ingredient", "volume")
            .containsExactlyInAnyOrder(tuple("test1", "test11"));

        Item optionalItem = item.getCategories().stream()
            .map(Category::getItem)
            .findFirst()
            .orElse(null);

        assertThat(optionalItem).isNotNull();
        assertThat(optionalItem.getId()).isEqualTo(item.getId());
    }

    @DisplayName("아이템을 조회하면서 view수를 늘릴 수 있다.")
    @Test
    void 아이템_조회수_증가_조회() throws Exception {
        //given
        var sample = fixtureMonkey.giveMeBuilder(ItemPostServiceDto.class)
            .set("categories", List.of(CategoryName.EYE))
            .set("nutritionFacts", List.of(new NutritionFactPostDto("test1", "test11")))
            .sample();

        var item = itemService.createItem(sample);

        //when
        IntStream.range(0, 100).forEach(
            i -> itemService.findItemWithAddingView(item.getId())
        );

        Item result = itemService.findItemWithAddingView(item.getId());

        //then
        assertThat(result.getView()).isEqualTo(101);
    }

    @Disabled
    @DisplayName("판매량이 높은 9개를 조회할 수 있다.")
    @Test
    void 판매량_9() throws Exception {
        //given

        var value = new AtomicInteger(1);
        var items = fixtureMonkey.giveMeBuilder(Item.class)
            .set("id", null)
            .setLazy("sales", () -> value.addAndGet(1))
            .setLazy("title", () -> "title" + value)
            .set("nutritionFacts", new ArrayList<>())
            .set("reviews", null)
            .set("brand", Brand.MYNI)
            .set("wishList", null)
            .set("categories", new ArrayList<>())
            .sampleList(15);

        itemRepository.saveAll(items);

        //when
        List<Item> top9SaleItems = itemQueryService.findTop9SaleItems();

        //then
        assertThat(top9SaleItems).hasSize(9)
            .isSortedAccordingTo(Comparator.comparing(Item::getSales).reversed())
            .extracting("title")
            .containsExactly("title16",
                "title15",
                "title14",
                "title13",
                "title12",
                "title11",
                "title10",
                "title9",
                "title8"
            );
    }

    @Disabled
    @DisplayName("할인율이 높은 9개를 조회할 수 있다.")
    @Test
    void 할인율_9() throws Exception {
        //given
        var value = new AtomicReference<Double>(1D);
        var items = fixtureMonkey.giveMeBuilder(Item.class)
            .set("id", null)
            .setLazy("itemPrice.discountRate", () -> value.getAndSet(value.get() + 1D))
            .setLazy("title", () -> "title" + value)
            .set("nutritionFacts", new ArrayList<>())
            .set("reviews", null)
            .set("brand", Brand.MYNI)
            .set("wishList", null)
            .set("categories", new ArrayList<>())
            .sampleList(15);

        itemRepository.saveAll(items);

        //when
        List<Item> top9SaleItems = itemQueryService.findTop9DiscountItems();

        //then
        assertThat(top9SaleItems).hasSize(9)
            .isSortedAccordingTo(Comparator.comparing(Item::getDiscountRate).reversed())
            .extracting("title")
            .containsExactly("title16.0",
                "title15.0",
                "title14.0",
                "title13.0",
                "title12.0",
                "title11.0",
                "title10.0",
                "title9.0",
                "title8.0"
            );
    }

    @Disabled
    @DisplayName("아이템 이름을 통해 조회할 수 있다.")
    @Test
    void 아이템_이름_조회() throws Exception {

        var value1 = new AtomicInteger(1);
        var items1 = fixtureMonkey.giveMeBuilder(Item.class)
            .set("id", null)
            .setLazy("title", () -> "title" + value1.addAndGet(1))
            .set("nutritionFacts", new ArrayList<>())
            .set("reviews", null)
            .set("brand", Brand.MYNI)
            .set("wishList", null)
            .set("categories", new ArrayList<>())
            .sampleList(5);

        var value2 = new AtomicInteger(9);
        var items2 = fixtureMonkey.giveMeBuilder(Item.class)
            .set("id", null)
            .setLazy("title", () -> "test" + value2.addAndGet(1))
            .set("nutritionFacts", new ArrayList<>())
            .set("reviews", null)
            .set("brand", Brand.MYNI)
            .set("wishList", null)
            .set("categories", new ArrayList<>())
            .sampleList(3);

        items1.addAll(items2);
        itemRepository.saveAll(items1);
        var dto = new ItemPageDto();
        dto.setPage(1);
        dto.setSize(14);

        //when
        Page<Item> items = itemQueryService.searchItems("test", ItemSearchRequest.to(dto));

        //then
        assertThat(items.getContent())
            .hasSize(3)
            .isSortedAccordingTo(Comparator.comparing(Item::getSales).reversed())
            .extracting("title")
            .containsExactlyInAnyOrder("test12", "test11", "test10");
        assertThat(items.getSize()).isEqualTo(16);
        assertThat(items.getTotalElements()).isEqualTo(3);
        assertThat(items.getTotalPages()).isEqualTo(1);
    }
}