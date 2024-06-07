package com.team33.moduleapi.ui.item;

import java.util.List;

import javax.validation.constraints.NotNull;

import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.team33.moduleapi.dto.MultiResponseDto;
import com.team33.moduleapi.dto.SingleResponseDto;
import com.team33.moduleapi.ui.item.dto.ItemDetailResponseDto;
import com.team33.moduleapi.ui.item.dto.ItemMainResponseDto;
import com.team33.moduleapi.ui.item.dto.ItemPageRequestDto;
import com.team33.moduleapi.ui.item.mapper.ItemQueryServiceMapper;
import com.team33.modulecore.category.domain.CategoryName;
import com.team33.modulecore.item.application.ItemCommandService;
import com.team33.modulecore.item.application.ItemQueryService;
import com.team33.modulecore.item.domain.entity.Item;
import com.team33.modulecore.item.dto.query.ItemPage;
import com.team33.modulecore.item.dto.query.ItemQueryDto;
import com.team33.modulecore.item.dto.query.PriceFilter;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/items")
public class ItemQueryController {

	private final ItemQueryServiceMapper itemQueryServiceMapper;
	private final ItemCommandService itemCommandService;
	private final ItemQueryService itemQueryService;

	@GetMapping("/main")
	public SingleResponseDto<ItemMainResponseDto> getMainItem() {

		List<ItemQueryDto> mainDiscountItems = itemQueryService.findMainDiscountItems();
		List<ItemQueryDto> mainSaleItems = itemQueryService.findMainSaleItems();

		return new SingleResponseDto<>(new ItemMainResponseDto(mainSaleItems, mainDiscountItems));
	}

	@GetMapping("/{itemId}")
	public SingleResponseDto<ItemDetailResponseDto> getItem(
		@NotNull @PathVariable Long itemId
	) {
		Item item = itemCommandService.getAndIncreaseView(itemId);
		return new SingleResponseDto<>(ItemDetailResponseDto.of(item));
	}

	@GetMapping("/search")
	public MultiResponseDto<ItemQueryDto> filteredItems(
		@RequestParam(required = false, defaultValue = "") String keyword,
		@RequestParam(required = false, defaultValue = "0") int low,
		@RequestParam(required = false, defaultValue = "0") int high,
		@RequestBody ItemPageRequestDto pageDto
	) {
		PriceFilter priceFilter = itemQueryServiceMapper.toPriceFilterDto(low, high);
		ItemPage searchDto = itemQueryServiceMapper.toItemPageDto(pageDto);

		Page<ItemQueryDto> itemsPage = itemQueryService.findFilteredItem(
			keyword.replace("_", ""),
			priceFilter,
			searchDto
		);

		return new MultiResponseDto<>(itemsPage.getContent(), itemsPage);
	}

	@GetMapping("/on-sale")
	public MultiResponseDto<ItemQueryDto> searchSaleItems(
		@RequestParam(required = false, defaultValue = "") String keyword,
		@RequestParam(required = false, defaultValue = "0") int low,
		@RequestParam(required = false, defaultValue = "0") int high,
		@RequestBody ItemPageRequestDto pageDto
	) {

		PriceFilter priceFilter = itemQueryServiceMapper.toPriceFilterDto(low, high);
		ItemPage searchDto = itemQueryServiceMapper.toItemPageDto(pageDto);

		Page<ItemQueryDto> itemsPage = itemQueryService.findItemOnSale(
			keyword.replace("_", ""),
			searchDto,
			priceFilter
		);

		return new MultiResponseDto<>(itemsPage.getContent(), itemsPage);
	}

	@GetMapping("/categories")
	public MultiResponseDto<ItemQueryDto> searchCategories(
		@RequestParam CategoryName categoryName,
		@RequestParam(required = false, defaultValue = "") String keyword,
		@RequestParam(required = false, defaultValue = "0") int low,
		@RequestParam(required = false, defaultValue = "0") int high,
		@RequestBody ItemPageRequestDto pageDto
	) {
		PriceFilter priceFilter = itemQueryServiceMapper.toPriceFilterDto(low, high);
		ItemPage searchDto = itemQueryServiceMapper.toItemPageDto(pageDto);

		Page<ItemQueryDto> itemsPage = itemQueryService.findByCategory(
			categoryName,
			keyword.replace("_", ""),
			priceFilter,
			searchDto
		);

		return new MultiResponseDto<>(itemsPage.getContent(), itemsPage);
	}

	@GetMapping("/brands")
	public MultiResponseDto<ItemQueryDto> searchBrand(
		@RequestParam(defaultValue = "") String brand,
		@RequestParam(required = false, defaultValue = "0") int low,
		@RequestParam(required = false, defaultValue = "0") int high,
		@RequestBody ItemPageRequestDto pageDto
	) {

		PriceFilter priceFilter = itemQueryServiceMapper.toPriceFilterDto(low, high);
		ItemPage searchDto = itemQueryServiceMapper.toItemPageDto(pageDto);

		Page<ItemQueryDto> itemsPage = itemQueryService.findByBrand(brand, searchDto, priceFilter);

		return new MultiResponseDto<>(itemsPage.getContent(), itemsPage);
	}
}

