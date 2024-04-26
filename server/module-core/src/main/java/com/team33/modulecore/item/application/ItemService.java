package com.team33.modulecore.item.application;


import com.team33.modulecore.category.application.CategoryService;
import com.team33.modulecore.category.domain.Category;
import com.team33.modulecore.exception.BusinessLogicException;
import com.team33.modulecore.exception.ExceptionCode;
import com.team33.modulecore.item.domain.Brand;
import com.team33.modulecore.item.domain.Item;
import com.team33.modulecore.item.domain.NutritionFact;
import com.team33.modulecore.item.domain.repository.ItemRepository;
import com.team33.modulecore.item.dto.ItemPostServiceDto;
import java.util.List;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Transactional
@Service
public class ItemService {

    private final ItemRepository itemRepository;
    private final BrandService brandService;
    private final NutritionFactService nutritionFactService;
    private final CategoryService categoryService;

    public Item createItem(ItemPostServiceDto dto) {
        List<NutritionFact> nutritionFacts = nutritionFactService.getNutritionFacts(dto);

        List<Category> category = categoryService.getCategories(dto.getCategories());

        Item item = Item.create(dto, nutritionFacts, category);

        return itemRepository.save(item);
    }

    //TODO: 이것도 캐싱을 해놓고, 조회수는 나중에 푸시하는 느낌으로 해도 될 것 같은데
    public Item findItemWithAddingView(long itemId) {
        Item item = findVerifiedItem(itemId);
        item.addView();
        return item;
    }

    public Item findVerifiedItem(long itemId) {
        return itemRepository.findById(itemId)
            .orElseThrow(() -> new BusinessLogicException(ExceptionCode.ITEM_NOT_FOUND));
    }

    public Page<Item> findItems(String categoryName, int page, int size, String sort) {
        if (Objects.equals(sort, "priceH")) {
            sort = "discountPrice";
        }
        if (Objects.equals(sort, "priceL")) {
            sort = "discountPrice";
            Page<Item> lowSortItems = itemRepository.findAllByCategoryName(
                PageRequest.of(page, size, Sort.by(sort).ascending()), categoryName);
            return lowSortItems;
        } else {
            Page<Item> findItems = itemRepository.findAllByCategoryName(
                PageRequest.of(page, size, Sort.by(sort).descending()), categoryName);
            return findItems;
        }
    }


    public Page<Item> findBrandItems(String categoryName, Brand brand, int page, int size,
        String sort) {
        brandService.verifyExistBrand(brand);

        if (Objects.equals(sort, "priceH")) {
            sort = "discountPrice";
        }
        if (Objects.equals(sort, "priceL")) {
            sort = "discountPrice";
            Page<Item> lowSortItems = itemRepository.findAllCategoryNameAndBrand(
                PageRequest.of(page, size, Sort.by(sort).ascending()), categoryName, brand);
            return lowSortItems;
        } else {
            Page<Item> findBrandItems = itemRepository.findAllCategoryNameAndBrand(
                PageRequest.of(page, size, Sort.by(sort).descending()), categoryName, brand);
            return findBrandItems;
        }
    }

    public Page<Item> findSaleItems(String categoryName, int page, int size, String sort) {
        if (Objects.equals(sort, "priceH")) {
            sort = "discountPrice";
        }
        if (Objects.equals(sort, "priceL")) {
            sort = "discountPrice";
            Page<Item> lowSortItems = itemRepository.findAllCategoryNameAndDiscountRate(
                PageRequest.of(page, size, Sort.by(sort).ascending()), categoryName);
            return lowSortItems;
        } else {
            Page<Item> findSaleItem = itemRepository.findAllCategoryNameAndDiscountRate(
                PageRequest.of(page, size, Sort.by(sort).descending()), categoryName);
            return findSaleItem;
        }
    }

    public Page<Item> findBrandSaleItems(String categoryName, Brand brand, int page, int size,
        String sort) {
        brandService.verifyExistBrand(brand);
        if (Objects.equals(sort, "priceH")) {
            sort = "discountPrice";
        }
        if (Objects.equals(sort, "priceL")) {
            sort = "discountPrice";
            Page<Item> lowSortItems = itemRepository.findAllCategoryNameAndDiscountRateAndBrand(
                PageRequest.of(page, size, Sort.by(sort).ascending()), categoryName, brand);
            return lowSortItems;
        } else {
            Page<Item> findBrandSaleItem = itemRepository.findAllCategoryNameAndDiscountRateAndBrand(
                PageRequest.of(page, size, Sort.by(sort).descending()), categoryName, brand);
            return findBrandSaleItem;
        }
    }

    @Transactional(readOnly = true)
    public List<Item> findTop9DiscountItems() {
        return itemRepository.findTop9ByOrderByDiscountRateDesc();
    }

    @Transactional(readOnly = true)
    public List<Item> findTop9SaleItems() {
        return itemRepository.findTop9ByOrderBySalesDesc();
    }

    public Page<Item> searchItems(String keyword, int page, int size, String sort) {
        keyword = keyword.replace("_", " ");
        if (Objects.equals(sort, "priceH")) {
            sort = "discountPrice";
        }
        if (Objects.equals(sort, "priceL")) {
            sort = "discountPrice";
            Page<Item> lowSortItems = itemRepository.findByTitleContaining(
                PageRequest.of(page, size, Sort.by(sort).ascending()), keyword);

            return lowSortItems;
        } else {
            Page<Item> searchItems = itemRepository.findByTitleContaining(
                PageRequest.of(page, size, Sort.by(sort).descending()), keyword);

            return searchItems;
        }
    }

    public Page<Item> pricefilteredItems(int low, int high, int page, int size, String sort) {
        if (Objects.equals(sort, "priceH")) {
            sort = "discountPrice";
        }
        if (Objects.equals(sort, "priceL")) {
            sort = "discountPrice";
            Page<Item> lowSortItems = itemRepository.findByPriceBetween(
                PageRequest.of(page, size, Sort.by(sort).ascending()), low, high);

            return lowSortItems;
        } else {
            Page<Item> filteredItems = itemRepository.findByPriceBetween(
                PageRequest.of(page, size, Sort.by(sort).descending()), low, high);

            return filteredItems;
        }
    }

    public Page<Item> searchPriceFilteredItems(String keyword, int low, int high, int page,
        int size, String sort) {
        keyword = keyword.replace("_", " ");
        if (Objects.equals(sort, "priceH")) {
            sort = "discountPrice";
        }
        if (Objects.equals(sort, "priceL")) {
            sort = "discountPrice";
            Page<Item> lowSortItems = itemRepository.findByTitleContainingAndPriceBetween(
                PageRequest.of(page, size, Sort.by(sort).ascending()), keyword, low, high);

            return lowSortItems;
        } else {
            Page<Item> filteredItems = itemRepository.findByTitleContainingAndPriceBetween(
                PageRequest.of(page, size, Sort.by(sort).descending()), keyword, low, high);

            return filteredItems;
        }
    }

    public Page<Item> searchSaleItems(String keyword, int page, int size, String sort) {
        keyword = keyword.replace("_", " ");
        if (Objects.equals(sort, "priceH")) {
            sort = "discountPrice";
        }
        if (Objects.equals(sort, "priceL")) {
            sort = "discountPrice";
            Page<Item> lowSortItems = itemRepository.findByTitleContainingAndDiscountRateGreaterThan(
                PageRequest.of(page, size, Sort.by(sort).ascending()), keyword, 0);

            return lowSortItems;
        } else {
            Page<Item> itemPage = itemRepository.findByTitleContainingAndDiscountRateGreaterThan(
                PageRequest.of(page, size, Sort.by(sort).descending()), keyword, 0);

            return itemPage;
        }
    }

    public Page<Item> searchSalePriceFilteredItems(String keyword, int low, int high, int page,
        int size, String sort) {
        keyword = keyword.replace("_", " ");
        if (Objects.equals(sort, "priceH")) {
            sort = "discountPrice";
        }
        if (Objects.equals(sort, "priceL")) {
            sort = "discountPrice";
            Page<Item> lowSortItems = itemRepository.findByTitleContainingAndDiscountRateGreaterThanAndPriceBetween(
                PageRequest.of(page, size, Sort.by(sort).ascending()), keyword, 0, low, high);

            return lowSortItems;
        } else {
            Page<Item> itemPage = itemRepository.findByTitleContainingAndDiscountRateGreaterThanAndPriceBetween(
                PageRequest.of(page, size, Sort.by(sort).descending()), keyword, 0, low, high);

            return itemPage;
        }
    }

    public Page<Item> priceFilteredCategoryItems(String categoryName, int low, int high, int page,
        int size, String sort) {
        if (Objects.equals(sort, "priceH")) {
            sort = "discountPrice";
        }
        if (Objects.equals(sort, "priceL")) {
            sort = "discountPrice";
            Page<Item> lowSortItems = itemRepository.findByCategoryNameAndPriceBetween(
                PageRequest.of(page, size, Sort.by(sort).ascending()), categoryName, low, high);
            return lowSortItems;
        } else {
            Page<Item> itemPage = itemRepository.findByCategoryNameAndPriceBetween(
                PageRequest.of(page, size, Sort.by(sort).descending()), categoryName, low, high);
            return itemPage;
        }
    }

    public Page<Item> priceFilteredCategorySaleItems(String categoryName, int low, int high,
        int page, int size, String sort) {
        if (Objects.equals(sort, "priceH")) {
            sort = "discountPrice";
        }
        if (Objects.equals(sort, "priceL")) {
            sort = "discountPrice";
            Page<Item> lowSortItems = itemRepository.findByCategoryNameAndSaleAndPriceBetween(
                PageRequest.of(page, size, Sort.by(sort).ascending()), categoryName, low, high);
            return lowSortItems;
        } else {
            Page<Item> itemPage = itemRepository.findByCategoryNameAndSaleAndPriceBetween(
                PageRequest.of(page, size, Sort.by(sort).descending()), categoryName, low, high);
            return itemPage;
        }
    }

    public Page<Item> priceFilteredCategoryAndBrandItems(String categoryName, Brand brand, int low,
        int high, int page, int size, String sort) {
        if (Objects.equals(sort, "priceH")) {
            sort = "discountPrice";
        }
        if (Objects.equals(sort, "priceL")) {
            sort = "discountPrice";
            Page<Item> lowSortItems = itemRepository.findByCategoryNameAndBrandAndPriceBetween(
                PageRequest.of(page, size, Sort.by(sort).ascending()), categoryName, brand, low,
                high);
            return lowSortItems;
        } else {
            Page<Item> itemPage = itemRepository.findByCategoryNameAndBrandAndPriceBetween(
                PageRequest.of(page, size, Sort.by(sort).descending()), categoryName, brand, low,
                high);
            return itemPage;
        }
    }

    public Page<Item> priceFilteredCategoryAndBrandAndSaleItems(String categoryName, Brand brand,
        int low, int high, int page, int size, String sort) {
        if (Objects.equals(sort, "priceH")) {
            sort = "discountPrice";
        }
        if (Objects.equals(sort, "priceL")) {
            sort = "discountPrice";
            Page<Item> lowSortItems = itemRepository.findByCategoryNameAndBrandAndSaleAndPriceBetween(
                PageRequest.of(page, size, Sort.by(sort).ascending()), categoryName, brand, low,
                high);
            return lowSortItems;
        } else {
            Page<Item> itemPage = itemRepository.findByCategoryNameAndBrandAndSaleAndPriceBetween(
                PageRequest.of(page, size, Sort.by(sort).descending()), categoryName, brand, low,
                high);
            return itemPage;
        }
    }


    public void deleteItem(long itemId) {
        Item verifiedItem = findVerifiedItem(itemId);
        itemRepository.delete(verifiedItem);
    }

}
