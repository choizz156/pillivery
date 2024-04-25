package com.team33.moduleapi.ui.item;

import com.team33.moduleapi.dto.SingleResponseDto;
import com.team33.modulecore.item.application.ItemService;
import com.team33.modulecore.item.domain.Item;
import com.team33.modulecore.item.dto.ItemDetailResponse;
import com.team33.modulecore.item.dto.ItemPostDto;
import com.team33.modulecore.item.dto.ItemPostServiceDto;
import com.team33.modulecore.review.application.ReviewService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
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
//    private final TalkService talkService;
//    private final ReviewMapper reviewMapper;
//    private final TalkMapper talkMapper;
//    private final ItemMapper mapper;

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/items")
    public SingleResponseDto<?> postItem(@RequestBody ItemPostDto itemPostDto) {
        ItemPostServiceDto dto = ItemPostServiceDto.to(itemPostDto);

        Item item = itemService.createItem(dto);

        ItemDetailResponse itemDetailResponse = ItemDetailResponse.of(item);

        return new SingleResponseDto<>(itemDetailResponse);
    }


//    @DeleteMapping("/items/{item-id}")
//    public ResponseEntity deleteItem(@PathVariable("item-id") long itemId) {
//        itemService.deleteItem(itemId);
//        return new ResponseEntity(HttpStatus.NO_CONTENT);
//    }
//
//
//    @GetMapping("/items/{item-id}")
//    public ResponseEntity getItem(@PathVariable("item-id") long itemId,
//        @RequestParam(value = "reviewPage", defaultValue = "1") int reviewPage,
//        @RequestParam(value = "talkPage", defaultValue = "1") int talkPage) {
//        Item item = itemService.findItem(itemId);
//        return new ResponseEntity(new SingleResponseDto<>(mapper.itemToItemDetailResponseDto(
//            item, reviewService, reviewMapper, talkService, talkMapper, reviewPage - 1, 5,
//            talkPage - 1, 5)), HttpStatus.OK);
//    }
//
//
//    @GetMapping("/main") // 메인화면에서 best 제품 9개 , 할인제품 9개 조회하기, MD pick(최신순) 9개 조회하기
//    public ResponseEntity getMainItem() {
//        return new ResponseEntity(
//            new SingleResponseDto<>(mapper.itemToItemMainTop9ResponseDto(itemService)),
//            HttpStatus.OK);
//    }
//
//    @GetMapping("/search")
//    public ResponseEntity searchItems(@RequestParam("keyword") String keyword,
//        @Positive @RequestParam(value = "page", defaultValue = "1") int page,
//        @Positive @RequestParam(value = "size", defaultValue = "16") int size,
//        @RequestParam(value = "sort", defaultValue = "sales") String sort) { // 키워드 검색
//        Page<Item> itemPage = itemService.searchItems(keyword, page - 1, size, sort);
//        List<Item> itemList = itemPage.getContent();
//        return new ResponseEntity(
//            new MultiResponseDto<>(mapper.itemsToItemCategoryResponseDto(itemList), itemPage),
//            HttpStatus.OK);
//    }
//
//    @GetMapping("/price")
//    public ResponseEntity priceFilteredItems(@RequestParam("low") int low,
//        @RequestParam("high") int high,
//        @Positive @RequestParam(value = "page", defaultValue = "1") int page,
//        @Positive @RequestParam(value = "size", defaultValue = "16") int size,
//        @RequestParam(value = "sort", defaultValue = "sales") String sort) { // 가격 필터
//        Page<Item> itemPage = itemService.pricefilteredItems(low, high, page - 1, size, sort);
//        List<Item> itemList = itemPage.getContent();
//        return new ResponseEntity(
//            new MultiResponseDto<>(mapper.itemsToItemCategoryResponseDto(itemList), itemPage),
//            HttpStatus.OK);
//    }
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

