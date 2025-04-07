package com.team33.modulecore.core.item.application;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import com.team33.modulecore.cache.CacheClient;
import com.team33.modulecore.cache.dto.CachedCategoryItems;
import com.team33.modulecore.cache.dto.CachedItems;
import com.team33.modulecore.core.category.domain.CategoryName;
import com.team33.modulecore.core.item.domain.entity.Item;
import com.team33.modulecore.core.item.domain.repository.ItemQueryRepository;
import com.team33.modulecore.core.item.dto.query.ItemPage;
import com.team33.modulecore.core.item.dto.query.ItemQueryDto;
import com.team33.modulecore.core.item.dto.query.PriceFilter;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class ItemQueryService {

	private final CacheClient cacheClient;
	private final ItemQueryRepository itemQueryRepository;

	public Item findItemById(long itemId) {

		return itemQueryRepository.findById(itemId);
	}

	public ItemQueryDto findItemQueryDtoById(long itemId) {

		Item item = itemQueryRepository.findById(itemId);
		return ItemQueryDto.builder()
			.itemId(item.getId())
			.enterprise(item.getInformation().getEnterprise())
			.thumbnail(item.getThumbnailUrl())
			.productName(item.getProductName())
			.realPrice(item.getRealPrice())
			.discountRate(item.getDiscountRate())
			.discountPrice(item.getDiscountPrice())
			.build();

	}

	public List<ItemQueryDto> findMainDiscountItems() {

		CachedItems<ItemQueryDto> cachedItems = cacheClient.getMainDiscountItem();
		return cachedItems.getCachedItems();
	}

	public List<ItemQueryDto> findMainSaleItems() {

		CachedItems<ItemQueryDto> cachedItems = cacheClient.getMainSalesItem();
		return cachedItems.getCachedItems();
	}

	public Page<ItemQueryDto> findFilteredItem(
		String keyword,
		PriceFilter priceFilter,
		ItemPage pageDto
	) {

		return itemQueryRepository.findFilteredItems(keyword, priceFilter, pageDto);
	}

	public Page<ItemQueryDto> findItemOnSale(
		String keyword,
		ItemPage pageDto,
		PriceFilter priceFilter
	) {

		return itemQueryRepository.findItemsOnSale(keyword, priceFilter, pageDto);
	}

	public Page<ItemQueryDto> findByCategory(
		CategoryName categoryName,
		String keyword,
		PriceFilter priceFilter,
		ItemPage pageDto
	) {

		CachedCategoryItems<ItemQueryDto> categoryItems =
			cacheClient.getCategoryItems(categoryName, keyword, priceFilter, pageDto);
		return categoryItems.toPage();
	}

	public Page<ItemQueryDto> findByBrand(String brand, ItemPage searchDto, PriceFilter priceFilter) {

		return itemQueryRepository.findByBrand(brand, searchDto, priceFilter);
	}
}
