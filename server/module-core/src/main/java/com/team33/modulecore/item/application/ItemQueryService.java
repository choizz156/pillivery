package com.team33.modulecore.item.application;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.team33.modulecore.category.domain.CategoryName;
import com.team33.modulecore.item.domain.repository.ItemQueryRepository;
import com.team33.modulecore.item.dto.ItemResponseDto;
import com.team33.modulecore.item.dto.query.ItemPageDto;
import com.team33.modulecore.item.dto.query.ItemQueryDto;
import com.team33.modulecore.item.dto.query.PriceFilterDto;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class ItemQueryService {

	private final ItemQueryRepository itemQueryRepository;

	public List<ItemQueryDto> findMainDiscountItems() {
		return itemQueryRepository.findItemsWithDiscountRateTop9();
	}

	public List<ItemQueryDto> findMainSaleItems() {
		return itemQueryRepository.findItemsWithSalesTop9();
	}

	public Page<ItemResponseDto> findFilteredItem(
		String keyword,
		PriceFilterDto priceFilterDto,
		ItemPageDto pageDto
	) {
		Page<ItemQueryDto> itemsByPrice =
			itemQueryRepository.findFilteredItems(keyword, priceFilterDto, pageDto);

		List<ItemResponseDto> itemResponseDtos = itemsByPrice.getContent().stream()
			.map(ItemResponseDto::from)
			.collect(Collectors.toUnmodifiableList());

		return new PageImpl<>(
			itemResponseDtos,
			PageRequest.of(itemsByPrice.getNumber() - 1, itemsByPrice.getSize(),
				itemsByPrice.getSort()),
			itemsByPrice.getTotalElements()
		);
	}

	// public Page<ItemResponseDto> findByCategory(CategoryName categoryName, ItemPageDto pageDto) {
	// 	Page<ItemQueryDto> itemsByCategory = itemQueryRepository.findItemsByCategory(categoryName, keyword,
	// 		priceFilterDto, pageDto);
	//
	// 	List<ItemResponseDto> itemResponseDtos = itemsByCategory.getContent().stream()
	// 		.map(ItemResponseDto::from)
	// 		.collect(Collectors.toUnmodifiableList());
	//
	// 	return new PageImpl<>(
	// 		itemResponseDtos,
	// 		PageRequest.of(itemsByCategory.getNumber() - 1, itemsByCategory.getSize(), itemsByCategory.getSort()),
	// 		itemsByCategory.getTotalElements()
	// 	);
	// }

	public Page<ItemResponseDto> findItemOnSale(
		String keyword,
		ItemPageDto pageDto,
		PriceFilterDto priceFilterDto
	) {
		Page<ItemQueryDto> itemsOnSale = itemQueryRepository.findItemsOnSale(keyword, priceFilterDto, pageDto);

		List<ItemResponseDto> itemResponseDtos = itemsOnSale.getContent().stream()
			.map(ItemResponseDto::from)
			.collect(Collectors.toUnmodifiableList());

		return new PageImpl<>(
			itemResponseDtos,
			PageRequest.of(itemsOnSale.getNumber() - 1, itemsOnSale.getSize(), itemsOnSale.getSort()),
			itemsOnSale.getTotalElements()
		);
	}

	public Page<ItemResponseDto> findByCategory(
		CategoryName categoryName,
		String keyword,
		PriceFilterDto priceFilterDto,
		ItemPageDto pageDto
	) {
		Page<ItemQueryDto> itemsByCategory = itemQueryRepository.findItemsByCategory(categoryName, keyword, priceFilterDto,pageDto);

		List<ItemResponseDto> itemResponseDtos = itemsByCategory.getContent().stream()
			.map(ItemResponseDto::from)
			.collect(Collectors.toUnmodifiableList());

		return new PageImpl<>(
			itemResponseDtos,
			PageRequest.of(itemsByCategory.getNumber() - 1, itemsByCategory.getSize(), itemsByCategory.getSort()),
			itemsByCategory.getTotalElements()
		);
	}
}
