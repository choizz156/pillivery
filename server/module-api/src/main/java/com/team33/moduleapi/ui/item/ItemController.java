package com.team33.moduleapi.ui.item;

import com.team33.moduleapi.dto.MultiResponseDto;
import com.team33.moduleapi.dto.SingleResponseDto;
import com.team33.modulecore.item.application.ItemQueryService;
import com.team33.modulecore.item.application.ItemCommandService;
import com.team33.modulecore.item.domain.entity.Item;
import com.team33.modulecore.item.dto.query.ItemDetailResponseDto;
import com.team33.modulecore.item.dto.query.ItemMainTop9ResponseDto;
import com.team33.modulecore.item.dto.query.ItemPageDto;
import com.team33.modulecore.item.dto.ItemPageRequestDto;
import com.team33.modulecore.item.dto.ItemPriceRequstDto;
import com.team33.modulecore.item.dto.ItemResponseDto;
import com.team33.modulecore.item.dto.query.PriceFilterDto;
import com.team33.modulecore.item.dto.query.ItemQueryDto;
import com.team33.modulecore.review.application.ReviewService;
import java.util.List;
import javax.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/items")
public class ItemController {

    private final ItemCommandService itemCommandService;
    private final ReviewService reviewService;
    private final ItemQueryService itemQueryService;
//    private final TalkService talkService;
//    private final ReviewMapper reviewMapper;
//    private final TalkMapper talkMapper;
//    private final ItemMapper mapper;

    @GetMapping("/main") // 메인화면에서 best 제품 9개 , 할인제품 9개 조회하기
    public SingleResponseDto<ItemMainTop9ResponseDto> getMainItem() {
        List<ItemQueryDto> top9DiscountItems = itemQueryService.findTop9DiscountItems();
        List<ItemQueryDto> top9SaleItems = itemQueryService.findTop9SaleItems();

        return new SingleResponseDto<>(ItemMainTop9ResponseDto.from(top9SaleItems, top9SaleItems));
    }

    @GetMapping("/{itemId}")
    public SingleResponseDto<ItemDetailResponseDto> getItem(@NotNull @PathVariable Long itemId) {
        Item item = itemCommandService.findItemWithAddingView(itemId);

        return new SingleResponseDto<>(ItemDetailResponseDto.of(item));
    }

    @GetMapping("/search")
    public MultiResponseDto<ItemResponseDto> priceFilteredItems(
        @RequestParam(required = false) String keyword,
        ItemPageRequestDto pageDto,
        ItemPriceRequstDto itemPriceRequstDto
    ) {
        PriceFilterDto priceFilterDto = PriceFilterDto.from(itemPriceRequstDto);
        ItemPageDto searchDto = ItemPageDto.from(pageDto);
        Page<ItemResponseDto> itemsPage = itemQueryService.findFilteredItem(
            keyword.replace("_", ""),
            priceFilterDto,
            searchDto
        );

        return new MultiResponseDto<>(itemsPage.getContent(), itemsPage);
    }


    @GetMapping("/on-sale")
    public MultiResponseDto<ItemResponseDto> searchSaleItems(ItemPageRequestDto pageDto) {
        ItemPageDto searchDto = ItemPageDto.from(pageDto);
        Page<ItemResponseDto> itemsPage = itemQueryService.findItemOnSale(searchDto);

        return new MultiResponseDto<>(itemsPage.getContent(), itemsPage);
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

