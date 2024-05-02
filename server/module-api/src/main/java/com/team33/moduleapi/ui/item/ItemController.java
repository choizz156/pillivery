package com.team33.moduleapi.ui.item;

import com.team33.moduleapi.dto.MultiResponseDto;
import com.team33.moduleapi.dto.SingleResponseDto;
import com.team33.modulecore.item.application.ItemQueryService;
import com.team33.modulecore.item.application.ItemService;
import com.team33.modulecore.item.domain.entity.Item;
import com.team33.modulecore.item.dto.ItemDetailResponseDto;
import com.team33.modulecore.item.dto.ItemMainTop9ResponseDto;
import com.team33.modulecore.item.dto.ItemPageRequestDto;
import com.team33.modulecore.item.dto.ItemPostDto;
import com.team33.modulecore.item.dto.ItemPostServiceDto;
import com.team33.modulecore.item.dto.ItemPriceRequstDto;
import com.team33.modulecore.item.dto.ItemResponseDto;
import com.team33.modulecore.item.dto.ItemPageDto;
import com.team33.modulecore.item.dto.PriceFilterDto;
import com.team33.modulecore.review.application.ReviewService;
import java.util.List;
import javax.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
    public SingleResponseDto<ItemDetailResponseDto> postItem(@RequestBody ItemPostDto itemPostDto) {
        ItemPostServiceDto dto = ItemPostServiceDto.to(itemPostDto);

        Item item = itemService.createItem(dto);

        ItemDetailResponseDto itemDetailResponseDto = ItemDetailResponseDto.of(item);

        return new SingleResponseDto<>(itemDetailResponseDto);
    }


    @GetMapping("/main") // 메인화면에서 best 제품 9개 , 할인제품 9개 조회하기
    public SingleResponseDto<ItemMainTop9ResponseDto> getMainItem() {
        List<Item> top9DiscountItems = itemQueryService.findTop9DiscountItems();
        List<Item> top9SaleItems = itemQueryService.findTop9SaleItems();

        return new SingleResponseDto<>(ItemMainTop9ResponseDto.from(top9SaleItems, top9SaleItems));
    }

    @GetMapping("/{itemId}")
    public SingleResponseDto<ItemDetailResponseDto> getItem(@NotNull @PathVariable Long itemId) {
        Item item = itemService.findItemWithAddingView(itemId);

        return new SingleResponseDto<>(ItemDetailResponseDto.of(item));
    }

    @GetMapping("/search")
    public MultiResponseDto<ItemResponseDto> priceFilteredItems(
        @RequestParam(required = false) String keyword,
        ItemPageRequestDto pageDto,
        ItemPriceRequstDto itemPriceRequstDto
    ) {
        PriceFilterDto priceFilterDto = PriceFilterDto.to(itemPriceRequstDto);
        ItemPageDto searchDto = ItemPageDto.to(pageDto);
        Page<ItemResponseDto> itemsPage = itemQueryService.findFilteredItem(
            keyword,
            priceFilterDto,
            searchDto
        );

        return new MultiResponseDto<>(itemsPage.getContent(), itemsPage);
    }


    @GetMapping("/on-sale")
    public ResponseEntity searchSaleItems(ItemPageRequestDto pageDto) {
        ItemPageDto searchDto = ItemPageDto.to(pageDto);
        Page<ItemResponseDto> itemsPage = itemQueryService.findItemOnSale(searchDto);

        return null;
//        List<Item> itemList = itemPage.getContent();
//        return new ResponseEntity<>(
//            new MultiResponseDto<>(mapper.itemsToItemCategoryResponseDto(itemList), itemPage),
//            HttpStatus.OK);
    }
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

