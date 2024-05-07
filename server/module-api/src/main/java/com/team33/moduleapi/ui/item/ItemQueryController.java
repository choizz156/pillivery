package com.team33.moduleapi.ui.item;

import java.util.List;

import javax.validation.constraints.NotNull;

import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.team33.moduleapi.dto.MultiResponseDto;
import com.team33.moduleapi.dto.SingleResponseDto;
import com.team33.modulecore.category.domain.CategoryName;
import com.team33.modulecore.item.application.ItemCommandService;
import com.team33.modulecore.item.application.ItemQueryService;
import com.team33.modulecore.item.domain.entity.Item;
import com.team33.modulecore.item.dto.ItemPageRequestDto;
import com.team33.modulecore.item.dto.ItemPriceRequstDto;
import com.team33.modulecore.item.dto.query.ItemDetailResponseDto;
import com.team33.modulecore.item.dto.query.ItemMainTop9ResponseDto;
import com.team33.modulecore.item.dto.query.ItemPageDto;
import com.team33.modulecore.item.dto.query.ItemQueryDto;
import com.team33.modulecore.item.dto.query.PriceFilterDto;
import com.team33.modulecore.review.application.ReviewService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/items")
public class ItemQueryController {

	private final ItemCommandService itemCommandService;
	private final ReviewService reviewService;
	private final ItemQueryService itemQueryService;
	//    private final TalkService talkService;
	//    private final ReviewMapper reviewMapper;

	@GetMapping("/main")
	public SingleResponseDto<ItemMainTop9ResponseDto> getMainItem() {

		List<ItemQueryDto> mainDiscountItems = itemQueryService.findMainDiscountItems();
		List<ItemQueryDto> mainSaleItems = itemQueryService.findMainSaleItems();

		return new SingleResponseDto<>(new ItemMainTop9ResponseDto(mainSaleItems, mainDiscountItems));
	}

	@GetMapping("/{itemId}")
	public SingleResponseDto<ItemDetailResponseDto> getItem(@NotNull @PathVariable Long itemId) {
		Item item = itemCommandService.increaseView(itemId);
		return new SingleResponseDto<>(ItemDetailResponseDto.of(item));
	}

	@GetMapping("/search")
	public MultiResponseDto<ItemQueryDto> filteredItems(
		@RequestParam(required = false) String keyword,
		ItemPageRequestDto pageDto,
		ItemPriceRequstDto itemPriceRequstDto
	) {

		PriceFilterDto priceFilterDto = PriceFilterDto.from(itemPriceRequstDto);
		ItemPageDto searchDto = ItemPageDto.from(pageDto);
		Page<ItemQueryDto> itemsPage = itemQueryService.findFilteredItem(
			keyword.replace("_", ""),
			priceFilterDto,
			searchDto
		);

		return new MultiResponseDto<>(itemsPage.getContent(), itemsPage);
	}

	@GetMapping("/on-sale")
	public MultiResponseDto<ItemQueryDto> searchSaleItems(
		@RequestParam(required = false) String keyword,
		ItemPageRequestDto pageRequestDto,
		ItemPriceRequstDto itemPriceRequstDto
	) {

		ItemPageDto pageDto = ItemPageDto.from(pageRequestDto);
		PriceFilterDto priceFilterDto = PriceFilterDto.from(itemPriceRequstDto);
		Page<ItemQueryDto> itemsPage = itemQueryService.findItemOnSale(keyword, pageDto, priceFilterDto);

		return new MultiResponseDto<>(itemsPage.getContent(), itemsPage);
	}

	@GetMapping("/categories")
	public MultiResponseDto<com.team33.modulecore.item.dto.query.ItemQueryDto> searchCategories(
		@RequestParam CategoryName categoryName,
		@RequestParam(required = false) String keyword,
		ItemPageRequestDto pageRequestDto,
		ItemPriceRequstDto itemPriceRequstDto
	) {
		ItemPageDto pageDto = ItemPageDto.from(pageRequestDto);
		PriceFilterDto priceFilterDto = PriceFilterDto.from(itemPriceRequstDto);
		Page<ItemQueryDto> itemsPage = itemQueryService.findByCategory(
			categoryName,
			keyword,
			priceFilterDto,
			pageDto
		);

		return new MultiResponseDto<>(itemsPage.getContent(), itemsPage);
	}
}

