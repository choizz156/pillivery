package team33.modulecore.domain.category.controller;


import java.util.List;
import javax.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import team33.modulecore.domain.category.dto.CategoryDto.Post;
import team33.modulecore.domain.category.entity.Category;
import team33.modulecore.domain.category.mapper.CategoryMapper;
import team33.modulecore.domain.category.service.CategoryService;
import team33.modulecore.domain.item.entity.Brand;
import team33.modulecore.domain.item.entity.Item;
import team33.modulecore.domain.item.mapper.ItemMapper;
import team33.modulecore.domain.item.service.ItemService;
import team33.modulecore.global.response.MultiResponseDto;
import team33.modulecore.global.response.SingleResponseDto;

@Slf4j
@Validated
@CrossOrigin
@RestController
@RequiredArgsConstructor
@RequestMapping("/category")
public class CategoryController {

    private final CategoryService categoryService;
    private final CategoryMapper categoryMapper;
    private final ItemMapper itemMapper;
    private final ItemService itemService;


    @PostMapping
    public ResponseEntity postCategory(@RequestBody Post post) {
        Category result = categoryService.createCategory(
            categoryMapper.categoryPostDtoToCategory(post));
        categoryService.verifyExistCategory(post.getCategoryName());
        return new ResponseEntity(
            new SingleResponseDto<>(categoryMapper.categoryToCategoryResponseDto(result)),
            HttpStatus.OK);
    }


    @GetMapping
    public ResponseEntity getCategoryItems(@RequestParam("categoryName") String categoryName,
        @Positive @RequestParam(value = "page", defaultValue = "1") int page,
        @Positive @RequestParam(value = "size", defaultValue = "16") int size,
        @RequestParam(value = "sort", defaultValue = "itemId") String sort) { // 카테고리별 아이템 목록 조회

        Page<Item> pageItems = itemService.findItems(categoryName, page - 1, size, sort);
        List<Item> items = pageItems.getContent();

        return new ResponseEntity(
            new MultiResponseDto<>(itemMapper.itemsToItemCategoryResponseDto(items), pageItems),
            HttpStatus.OK);
    }


    @GetMapping("/brand")
    public ResponseEntity getCategoryBrandItems(@RequestParam("categoryName") String categoryName,
        @RequestParam("brand") Brand brand,
        @Positive @RequestParam(value = "page", defaultValue = "1") int page,
        @Positive @RequestParam(value = "size", defaultValue = "16") int size,
        @RequestParam(value = "sort", defaultValue = "itemId") String sort) { // 카테고리별 브랜드별 아이템 목록 조회

        Page<Item> pageBrandItems = itemService.findBrandItems(categoryName, brand, page - 1, size,
            sort);
        List<Item> brandItems = pageBrandItems.getContent();
        return new ResponseEntity(
            new MultiResponseDto<>(itemMapper.itemsToItemCategoryResponseDto(brandItems),
                pageBrandItems), HttpStatus.OK);
    }


    @GetMapping("/sale")
    public ResponseEntity getCategorySaleItems(@RequestParam("categoryName") String categoryName,
        @Positive @RequestParam(value = "page", defaultValue = "1") int page,
        @Positive @RequestParam(value = "size", defaultValue = "16") int size,
        @RequestParam(value = "sort", defaultValue = "itemId") String sort
    ) { // 카테고리별 할인제품 모아보기
        Page<Item> pageSaleItems = itemService.findSaleItems(categoryName, page - 1, size, sort);
        List<Item> saleItems = pageSaleItems.getContent();
        return new ResponseEntity(
            new MultiResponseDto<>(itemMapper.itemsToItemCategoryResponseDto(saleItems),
                pageSaleItems), HttpStatus.OK);
    }


    @GetMapping("/brand/sale") // 카테고리별 브랜드별 할인제품 모아보기
    public ResponseEntity getCategoryBrandSaleItems(
        @RequestParam("categoryName") String categoryName,
        @RequestParam("brand") Brand brand,
        @Positive @RequestParam(value = "page", defaultValue = "1") int page,
        @Positive @RequestParam(value = "size", defaultValue = "16") int size,
        @RequestParam(value = "sort", defaultValue = "itemId") String sort) {
        Page<Item> pageBrandSaleItems = itemService.findBrandSaleItems(categoryName, brand,
            page - 1, size, sort);
        List<Item> brandSaleItems = pageBrandSaleItems.getContent();
        return new ResponseEntity(
            new MultiResponseDto<>(itemMapper.itemsToItemCategoryResponseDto(brandSaleItems),
                pageBrandSaleItems), HttpStatus.OK);
    }

    @GetMapping("/price")
    public ResponseEntity getCategoryPriceFilteredItems(
        @RequestParam("categoryName") String categoryName,
        @RequestParam("low") int low, @RequestParam("high") int high,
        @Positive @RequestParam(value = "page", defaultValue = "1") int page,
        @Positive @RequestParam(value = "size", defaultValue = "16") int size,
        @RequestParam(value = "sort", defaultValue = "sales") String sort) { // 카테고리 + 가격 필터

        Page<Item> itemPage = itemService.priceFilteredCategoryItems(categoryName, low, high,
            page - 1, size, sort);
        List<Item> itemList = itemPage.getContent();
        return new ResponseEntity(
            new MultiResponseDto<>(itemMapper.itemsToItemCategoryResponseDto(itemList), itemPage),
            HttpStatus.OK);
    }


    @GetMapping("/sale/price")
    public ResponseEntity getCategorySalePriceFilteredItems(
        @RequestParam("categoryName") String categoryName,
        @RequestParam("low") int low, @RequestParam("high") int high,
        @Positive @RequestParam(value = "page", defaultValue = "1") int page,
        @Positive @RequestParam(value = "size", defaultValue = "16") int size,
        @RequestParam(value = "sort", defaultValue = "sales") String sort) { // 카테고리 + 할인 + 가격필터
        Page<Item> itemPage = itemService.priceFilteredCategorySaleItems(categoryName, low, high,
            page - 1, size, sort);
        List<Item> itemList = itemPage.getContent();
        return new ResponseEntity(
            new MultiResponseDto<>(itemMapper.itemsToItemCategoryResponseDto(itemList), itemPage),
            HttpStatus.OK);
    }

    @GetMapping("/brand/price")
    public ResponseEntity getCategoryBrandPriceFilteredItems(
        @RequestParam("categoryName") String categoryName,
        @RequestParam("brand") Brand brand,
        @RequestParam("low") int low, @RequestParam("high") int high,
        @Positive @RequestParam(value = "page", defaultValue = "1") int page,
        @Positive @RequestParam(value = "size", defaultValue = "16") int size,
        @RequestParam(value = "sort", defaultValue = "sales") String sort) { // 카테고리 + 브랜드 + 가격필터
        Page<Item> itemPage = itemService.priceFilteredCategoryAndBrandItems(categoryName, brand,
            low, high, page - 1, size, sort);
        List<Item> itemList = itemPage.getContent();
        return new ResponseEntity(
            new MultiResponseDto<>(itemMapper.itemsToItemCategoryResponseDto(itemList), itemPage),
            HttpStatus.OK);
    }

    @GetMapping("/brand/sale/price")
    public ResponseEntity getCategoryBrandSaleFilteredItems(
        @RequestParam("categoryName") String categoryName,
        @RequestParam("brand") Brand brand,
        @RequestParam("low") int low, @RequestParam("high") int high,
        @Positive @RequestParam(value = "page", defaultValue = "1") int page,
        @Positive @RequestParam(value = "size", defaultValue = "16") int size,
        @RequestParam(value = "sort", defaultValue = "sales") String sort) { // 카테고리 + 브랜드 + 할인 + 가격필터
        Page<Item> itemPage = itemService.priceFilteredCategoryAndBrandAndSaleItems(categoryName,
            brand, low, high, page - 1, size, sort);
        List<Item> itemList = itemPage.getContent();
        return new ResponseEntity(
            new MultiResponseDto<>(itemMapper.itemsToItemCategoryResponseDto(itemList), itemPage),
            HttpStatus.OK);
    }

}
