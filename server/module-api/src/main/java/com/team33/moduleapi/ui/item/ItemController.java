package com.team33.moduleapi.ui.item;

import com.team33.moduleapi.dto.MultiResponseDto;
import com.team33.moduleapi.dto.SingleResponseDto;
import com.team33.modulecore.item.application.ItemQueryService;
import com.team33.modulecore.item.application.ItemService;
import com.team33.modulecore.item.domain.entity.Item;
import com.team33.modulecore.item.dto.ItemDetailResponseDto;
import com.team33.modulecore.item.dto.ItemMainTop9ResponseDto;
import com.team33.modulecore.item.dto.ItemPageDto;
import com.team33.modulecore.item.dto.ItemPostDto;
import com.team33.modulecore.item.dto.ItemPostServiceDto;
import com.team33.modulecore.item.dto.ItemResponseDto;
import com.team33.modulecore.item.dto.ItemSearchRequest;
import com.team33.modulecore.review.application.ReviewService;
import java.util.List;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RequiredArgsConstructor
@Validated
@RestController
@RequestMapping("/items")
public class ItemController {

    private final ItemService itemService;
    private final ReviewService reviewService;
    private final ItemQueryService itemQueryService;
//    private final TalkService talkService;
//    private final ReviewMapper reviewMapper;
//    private final TalkMapper talkMapper;
//    private final ItemMapper mapper;

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping
    public SingleResponseDto<?> postItem(@RequestBody ItemPostDto itemPostDto) {
        ItemPostServiceDto dto = ItemPostServiceDto.to(itemPostDto);

        Item item = itemService.createItem(dto);

        ItemDetailResponseDto itemDetailResponseDto = ItemDetailResponseDto.of(item);

        return new SingleResponseDto<>(itemDetailResponseDto);
    }


    @GetMapping("/main") // 메인화면에서 best 제품 9개 , 할인제품 9개 조회하기
    public SingleResponseDto<?> getMainItem() {
        List<Item> top9DiscountItems = itemQueryService.findTop9DiscountItems();
        List<Item> top9SaleItems = itemQueryService.findTop9SaleItems();

        return new SingleResponseDto<>(ItemMainTop9ResponseDto.from(top9SaleItems, top9SaleItems));
    }

    @GetMapping("/keywords")
    public MultiResponseDto searchItems(
        @NotNull @RequestParam String keywords,
        ItemPageDto pageDto
    ) {
        ItemSearchRequest dto = ItemSearchRequest.to(pageDto);

        Page<Item> itemPage = itemQueryService.searchItems(keywords, dto);
        List<Item> items = itemPage.getContent();

        return new MultiResponseDto<>(ItemResponseDto.from(items), itemPage);
    }

    //    @DeleteMapping("/items/{item-id}")
//    public ResponseEntity deleteItem(@PathVariable("item-id") long itemId) {
//        itemService.deleteItem(itemId);
//        return new ResponseEntity(HttpStatus.NO_CONTENT);
//    }

    @GetMapping("/{itemId}")
    public SingleResponseDto<?> getItem(@NotNull @PathVariable Long itemId) {
        Item item = itemService.findItemWithAddingView(itemId);
        return new SingleResponseDto<>(ItemDetailResponseDto.of(item));
    }

    @GetMapping("/prices")
    public MultiResponseDto priceFilteredItems(
        @Positive @NotNull @RequestParam("low") int low,
        @Positive @NotNull @RequestParam("high") int high,
        ItemPageDto pageDto
    ) {
        ItemSearchRequest dto = ItemSearchRequest.to(pageDto);
        Page<ItemResponseDto> itemsPage = itemQueryService.findFilteredItemByPrice(low, high, dto);
        return new MultiResponseDto<>(itemsPage.getContent(), itemsPage);
    }
//
//    @GetMapping("/search/price")
//    public ResponseEntity searchPriceFilteredItems(@RequestParam("keyword") String keyword,
//        @RequestParam("low") int low, @RequestParam("high") int high,
//        @Positive @RequestParam(value = "page", defaultValue = "1") int page,
//        @Positive @RequestParam(value = "size", defaultValue = "16") int size,
//        @RequestParam(value = "sort", defaultValue = "sales") String sort) { // 키워드 검색 + 가격 필터
//        Page<Item> itemPage = itemService.searchPriceFilteredItems(keyword, low, high, page - 1,
//            size, sort);
//        List<Item> itemList = itemPage.getContent();
//        return new ResponseEntity(
//            new MultiResponseDto<>(mapper.itemsToItemCategoryResponseDto(itemList), itemPage),
//            HttpStatus.OK);
//    }
//
//    @GetMapping("/search/sale")
//    public ResponseEntity searchSaleItems(@RequestParam("keyword") String keyword,
//        @Positive @RequestParam(value = "page", defaultValue = "1") int page,
//        @Positive @RequestParam(value = "size", defaultValue = "16") int size,
//        @RequestParam(value = "sort", defaultValue = "sales") String sort) { // 키워드 검색 + 세일
//        Page<Item> itemPage = itemService.searchSaleItems(keyword, page - 1, size, sort);
//        List<Item> itemList = itemPage.getContent();
//        return new ResponseEntity<>(
//            new MultiResponseDto<>(mapper.itemsToItemCategoryResponseDto(itemList), itemPage),
//            HttpStatus.OK);
//    }
//
//    @GetMapping("/search/sale/price")
//    public ResponseEntity searchSalePriceFilteredItems(@RequestParam("keyword") String keyword,
//        @RequestParam("low") int low, @RequestParam("high") int high,
//        @Positive @RequestParam(value = "page", defaultValue = "1") int page,
//        @Positive @RequestParam(value = "size", defaultValue = "16") int size,
//        @RequestParam(value = "sort", defaultValue = "sales") String sort) { // 키워드 검색 + 세일 + 가격 필터
//        Page<Item> itemPage = itemService.searchSalePriceFilteredItems(keyword, low, high, page - 1,
//            size, sort);
//        List<Item> itemList = itemPage.getContent();
//        return new ResponseEntity<>(
//            new MultiResponseDto<>(mapper.itemsToItemCategoryResponseDto(itemList), itemPage),
//            HttpStatus.OK);
//    }

}

