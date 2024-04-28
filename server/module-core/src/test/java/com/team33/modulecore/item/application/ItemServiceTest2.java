//package com.team33.modulecore.item.application;
//
//import static org.assertj.core.api.Assertions.assertThat;
//import static org.assertj.core.api.Assertions.tuple;
//import static org.mockito.ArgumentMatchers.any;
//import static org.mockito.ArgumentMatchers.anyList;
//import static org.mockito.BDDMockito.given;
//
//import com.navercorp.fixturemonkey.FixtureMonkey;
//import com.navercorp.fixturemonkey.api.introspector.FieldReflectionArbitraryIntrospector;
//import com.team33.modulecore.category.application.CategoryService;
//import com.team33.modulecore.category.domain.Category;
//import com.team33.modulecore.category.domain.CategoryName;
//import com.team33.modulecore.item.domain.entity.Item;
//import com.team33.modulecore.item.domain.repository.ItemRepository;
//import com.team33.modulecore.item.dto.ItemPostServiceDto;
//import com.team33.modulecore.item.dto.NutritionFactPostDto;
//import java.util.List;
//import org.junit.jupiter.api.DisplayName;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.mockito.InjectMocks;
//import org.mockito.junit.jupiter.MockitoExtension;
//import org.springframework.boot.test.mock.mockito.MockBean;
//
//@ExtendWith(MockitoExtension.class)
//public class ItemServiceTest2 {
//
//    @MockBean
//    private ItemService itemService;
//
//    @InjectMocks
//    private CategoryService categoryService;
//
//    @InjectMocks
//    private NutritionFactService nutritionFactService;
//
//    @MockBean
//    private ItemRepository itemRepository;
//
//    FixtureMonkey fixtureMonkey = FixtureMonkey.builder()
//        .objectIntrospector(FieldReflectionArbitraryIntrospector.INSTANCE)
//        .defaultNotNull(true)
//        .build();
//
//    @DisplayName("item을 저장할 수 있다.")
//    @Test
//    void 아이템_생성() throws Exception {
//        //given
//        Item item = fixtureMonkey.giveMeBuilder(Item.class).set("id", 1L).sample();
//
//        var sample = fixtureMonkey.giveMeBuilder(ItemPostServiceDto.class)
//            .set("categories", List.of(CategoryName.EYE, CategoryName.BONE))
//            .set("nutritionFacts", List.of(new NutritionFactPostDto("test1", "test11")))
//            .sample();
////
////        CategoryService categoryService = mock(CategoryService.class);
////        NutritionFactService nutritionFactService = mock(NutritionFactService.class);
////        ItemRepository itemRepository = mock(ItemRepository.class);
//
//        ItemService itemService = new ItemService(itemRepository, null, nutritionFactService,
//            categoryService);
//        given(categoryService.getCategories(anyList())).willReturn(
//            List.of(new Category(CategoryName.ANTIOXIDANT)));
//
//        given(itemService.createItem(any())).willReturn(item);
//
//        //when
//        Item result = itemService.createItem(sample);
//
//        //then
//        assertThat(item.getCategories()).hasSize(2)
//            .extracting("categoryName")
//            .containsExactlyInAnyOrder(CategoryName.EYE, CategoryName.BONE);
//
//        assertThat(item.getNutritionFacts()).hasSize(1)
//            .extracting("ingredient", "volume")
//            .containsExactlyInAnyOrder(tuple("test1", "test11"));
//
//        Item optionalItem = item.getCategories().stream()
//            .map(Category::getItem)
//            .findFirst()
//            .orElse(null);
//
//        assertThat(optionalItem).isNotNull();
//        assertThat(optionalItem.getId()).isEqualTo(item.getId());
//    }
//
//}
