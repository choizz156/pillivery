package com.team33.modulecore.item.application;


import com.team33.modulecore.category.application.CategoryService;
import com.team33.modulecore.category.domain.Category;
import com.team33.modulecore.exception.BusinessLogicException;
import com.team33.modulecore.exception.ExceptionCode;
import com.team33.modulecore.item.domain.entity.Item;
import com.team33.modulecore.item.domain.entity.ItemCategory;
import com.team33.modulecore.item.domain.entity.NutritionFact;
import com.team33.modulecore.item.domain.repository.ItemCommandRepository;
import com.team33.modulecore.item.domain.repository.ItemQueryRepository;
import com.team33.modulecore.item.dto.ItemPostServiceDto;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Transactional
@Service
public class ItemService {

    private final BrandService brandService;
    private final NutritionFactService nutritionFactService;
    private final CategoryService categoryService;
    private final ItemQueryRepository itemQueryRepository;
    private final ItemCommandRepository itemCommandRepository;

    public Item createItem(ItemPostServiceDto dto) {
        List<NutritionFact> nutritionFacts = nutritionFactService.getNutritionFacts(dto);

        List<Category> categories = categoryService.getCategories(dto.getCategories());

        Set<ItemCategory> itemCategoryList =
            categories.stream()
                .map(ItemCategory::of)
                .collect(Collectors.toSet());

        Item item = Item.create(dto, nutritionFacts, itemCategoryList);

        return itemCommandRepository.save(item);
    }

    //TODO: 이것도 캐싱을 해놓고, 조회수는 나중에 푸시하는 느낌으로 해도 될 것 같은데
    public Item findItemWithAddingView(long itemId) {
        Item item = itemCommandRepository.findById(itemId)
            .orElseThrow(() -> new BusinessLogicException(ExceptionCode.ITEM_NOT_FOUND));
        item.addView();
        return item;
    }

//
//
//    public Page<Item> findBrandItems(String categoryName, Brand brand, int page, int size,
//        String sort) {
//        brandService.verifyExistBrand(brand);
//
//        if (Objects.equals(sort, "priceH")) {
//            sort = "discountPrice";
//        }
//        if (Objects.equals(sort, "priceL")) {
//            sort = "discountPrice";
//            Page<Item> lowSortItems = itemJpaRepository.findAllCategoryNameAndBrand(
//                PageRequest.of(page, size, Sort.by(sort).ascending()), categoryName, brand);
//            return lowSortItems;
//        } else {
//            Page<Item> findBrandItems = itemJpaRepository.findAllCategoryNameAndBrand(
//                PageRequest.of(page, size, Sort.by(sort).descending()), categoryName, brand);
//            return findBrandItems;
//        }
//    }
//
//    public Page<Item> findSaleItems(String categoryName, int page, int size, String sort) {
//        if (Objects.equals(sort, "priceH")) {
//            sort = "discountPrice";
//        }
//        if (Objects.equals(sort, "priceL")) {
//            sort = "discountPrice";
//            Page<Item> lowSortItems = itemJpaRepository.findAllCategoryNameAndDiscountRate(
//                PageRequest.of(page, size, Sort.by(sort).ascending()), categoryName);
//            return lowSortItems;
//        } else {
//            Page<Item> findSaleItem = itemJpaRepository.findAllCategoryNameAndDiscountRate(
//                PageRequest.of(page, size, Sort.by(sort).descending()), categoryName);
//            return findSaleItem;
//        }
//    }
//
//    public Page<Item> findBrandSaleItems(String categoryName, Brand brand, int page, int size,
//        String sort) {
//        brandService.verifyExistBrand(brand);
//        if (Objects.equals(sort, "priceH")) {
//            sort = "discountPrice";
//        }
//        if (Objects.equals(sort, "priceL")) {
//            sort = "discountPrice";
//            Page<Item> lowSortItems = itemJpaRepository.findAllCategoryNameAndDiscountRateAndBrand(
//                PageRequest.of(page, size, Sort.by(sort).ascending()), categoryName, brand);
//            return lowSortItems;
//        } else {
//            Page<Item> findBrandSaleItem = itemJpaRepository.findAllCategoryNameAndDiscountRateAndBrand(
//                PageRequest.of(page, size, Sort.by(sort).descending()), categoryName, brand);
//            return findBrandSaleItem;
//        }
//    }

//
//    public Page<Item> priceFilteredCategoryItems(String categoryName, int low, int high, int page,
//        int size, String sort) {
//        if (Objects.equals(sort, "priceH")) {
//            sort = "discountPrice";
//        }
//        if (Objects.equals(sort, "priceL")) {
//            sort = "discountPrice";
//            Page<Item> lowSortItems = itemJpaRepository.findByCategoryNameAndPriceBetween(
//                PageRequest.of(page, size, Sort.by(sort).ascending()), categoryName, low, high);
//            return lowSortItems;
//        } else {
//            Page<Item> itemPage = itemJpaRepository.findByCategoryNameAndPriceBetween(
//                PageRequest.of(page, size, Sort.by(sort).descending()), categoryName, low, high);
//            return itemPage;
//        }
//    }
//
//    public Page<Item> priceFilteredCategorySaleItems(String categoryName, int low, int high,
//        int page, int size, String sort) {
//        if (Objects.equals(sort, "priceH")) {
//            sort = "discountPrice";
//        }
//        if (Objects.equals(sort, "priceL")) {
//            sort = "discountPrice";
//            Page<Item> lowSortItems = itemJpaRepository.findByCategoryNameAndSaleAndPriceBetween(
//                PageRequest.of(page, size, Sort.by(sort).ascending()), categoryName, low, high);
//            return lowSortItems;
//        } else {
//            Page<Item> itemPage = itemJpaRepository.findByCategoryNameAndSaleAndPriceBetween(
//                PageRequest.of(page, size, Sort.by(sort).descending()), categoryName, low, high);
//            return itemPage;
//        }
//    }
//
//    public Page<Item> priceFilteredCategoryAndBrandItems(String categoryName, Brand brand, int low,
//        int high, int page, int size, String sort) {
//        if (Objects.equals(sort, "priceH")) {
//            sort = "discountPrice";
//        }
//        if (Objects.equals(sort, "priceL")) {
//            sort = "discountPrice";
//            Page<Item> lowSortItems = itemJpaRepository.findByCategoryNameAndBrandAndPriceBetween(
//                PageRequest.of(page, size, Sort.by(sort).ascending()), categoryName, brand, low,
//                high);
//            return lowSortItems;
//        } else {
//            Page<Item> itemPage = itemJpaRepository.findByCategoryNameAndBrandAndPriceBetween(
//                PageRequest.of(page, size, Sort.by(sort).descending()), categoryName, brand, low,
//                high);
//            return itemPage;
//        }
//    }
//
//    public Page<Item> priceFilteredCategoryAndBrandAndSaleItems(String categoryName, Brand brand,
//        int low, int high, int page, int size, String sort) {
//        if (Objects.equals(sort, "priceH")) {
//            sort = "discountPrice";
//        }
//        if (Objects.equals(sort, "priceL")) {
//            sort = "discountPrice";
//            Page<Item> lowSortItems = itemJpaRepository.findByCategoryNameAndBrandAndSaleAndPriceBetween(
//                PageRequest.of(page, size, Sort.by(sort).ascending()), categoryName, brand, low,
//                high);
//            return lowSortItems;
//        } else {
//            Page<Item> itemPage = itemJpaRepository.findByCategoryNameAndBrandAndSaleAndPriceBetween(
//                PageRequest.of(page, size, Sort.by(sort).descending()), categoryName, brand, low,
//                high);
//            return itemPage;
//        }
//    }

//
//    public void deleteItem(long itemId) {
//        Item verifiedItem = findVerifiedItem(itemId);
//        itemRepository.delete(verifiedItem);
//    }
//
}
