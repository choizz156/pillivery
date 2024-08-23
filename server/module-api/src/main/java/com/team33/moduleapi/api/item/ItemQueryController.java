package com.team33.moduleapi.api.item;

import java.util.List;

import javax.validation.constraints.NotNull;

import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.team33.moduleapi.response.MultiResponseDto;
import com.team33.moduleapi.response.SingleResponseDto;
import com.team33.moduleapi.api.item.dto.ItemDetailResponseDto;
import com.team33.moduleapi.api.item.dto.ItemMainResponseDto;
import com.team33.moduleapi.api.item.mapper.ItemQueryServiceMapper;
import com.team33.modulecore.core.category.domain.CategoryName;
import com.team33.modulecore.core.item.application.ItemQueryService;
import com.team33.modulecore.core.item.domain.ItemSortOption;
import com.team33.modulecore.core.item.domain.entity.Item;
import com.team33.modulecore.core.item.dto.query.ItemPage;
import com.team33.modulecore.core.item.dto.query.ItemQueryDto;
import com.team33.modulecore.core.item.dto.query.PriceFilter;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/items")
public class ItemQueryController {

	private final ItemQueryServiceMapper itemQueryServiceMapper;
	private final ItemQueryService itemQueryService;

	@GetMapping("/main")
	public SingleResponseDto<ItemMainResponseDto> getMainItem() {

		List<ItemQueryDto> mainDiscountItems = itemQueryService.findMainDiscountItems();
		List<ItemQueryDto> mainSaleItems = itemQueryService.findMainSaleItems();

		return new SingleResponseDto<>(new ItemMainResponseDto(mainSaleItems, mainDiscountItems));
	}

	@GetMapping("/{itemId}")
	public SingleResponseDto<ItemDetailResponseDto> getItem(
		@NotNull @PathVariable long itemId
	) {
		Item item = itemQueryService.findItemById(itemId);

		return new SingleResponseDto<>(ItemDetailResponseDto.of(item));
	}

	@GetMapping("/search")
	public MultiResponseDto<ItemQueryDto> filteredItems(
		@RequestParam(required = false, defaultValue = "") String keyword,
		@RequestParam(required = false, defaultValue = "0") int low,
		@RequestParam(required = false, defaultValue = "0") int high,
		@RequestParam(defaultValue = "1") int page,
		@RequestParam(defaultValue = "8") int size,
		@RequestParam(defaultValue = "SALES") ItemSortOption sort
	) {
		PriceFilter priceFilter = itemQueryServiceMapper.toPriceFilterDto(low, high);
		ItemPage searchDto = itemQueryServiceMapper.toItemPageDto(page, size, sort);

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
		@RequestParam(defaultValue = "1") int page,
		@RequestParam(defaultValue = "8") int size,
		@RequestParam(defaultValue = "SALES") ItemSortOption sort
	) {

		PriceFilter priceFilter = itemQueryServiceMapper.toPriceFilterDto(low, high);
		ItemPage searchDto = itemQueryServiceMapper.toItemPageDto(page, size, sort);

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
		@RequestParam(defaultValue = "1") int page,
		@RequestParam(defaultValue = "8") int size,
		@RequestParam(defaultValue = "SALES") ItemSortOption sort
	) {
		PriceFilter priceFilter = itemQueryServiceMapper.toPriceFilterDto(low, high);
		ItemPage searchDto = itemQueryServiceMapper.toItemPageDto(page, size, sort);

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
		@RequestParam(defaultValue = "1") int page,
		@RequestParam(defaultValue = "8") int size,
		@RequestParam(defaultValue = "SALES") ItemSortOption sort
	) {

		PriceFilter priceFilter = itemQueryServiceMapper.toPriceFilterDto(low, high);
		ItemPage searchDto = itemQueryServiceMapper.toItemPageDto(page, size, sort);

		Page<ItemQueryDto> itemsPage = itemQueryService.findByBrand(brand, searchDto, priceFilter);

		return new MultiResponseDto<>(itemsPage.getContent(), itemsPage);
	}
}

