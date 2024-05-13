package com.team33.modulecore.item.domain.mock;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.data.domain.Page;

import com.team33.modulecore.FixtureMonkeyFactory;
import com.team33.modulecore.category.domain.CategoryName;
import com.team33.modulecore.item.domain.entity.Item;
import com.team33.modulecore.item.domain.repository.ItemQueryRepository;
import com.team33.modulecore.item.dto.query.ItemPage;
import com.team33.modulecore.item.dto.query.ItemQueryDto;
import com.team33.modulecore.item.dto.query.PriceFilter;

public class FakeItemQuerydslDao implements ItemQueryRepository {

	private Map<Long, ItemQueryDto> store;

	public FakeItemQuerydslDao() {
		this.store = new HashMap<>();
		store.put(1L, ItemQueryDto.builder()
			.productName("test")
			.build());
	}

	@Override
	public Item findById(long id) {
		return FixtureMonkeyFactory.get().giveMeBuilder(Item.class)
			.set("id", 1L)
			.setNull("itemCategory")
			.sample();
	}

	@Override
	public List<ItemQueryDto> findItemsWithSalesTop9() {
		return List.of();
	}

	@Override
	public List<ItemQueryDto> findItemsWithDiscountRateTop9() {
		return List.of();
	}

	@Override
	public Page<ItemQueryDto> findFilteredItems(String keyword, PriceFilter priceFilter, ItemPage pageDto) {
		return null;
	}

	@Override
	public Page<ItemQueryDto> findItemsOnSale(String keyword, PriceFilter priceFilter, ItemPage pageDto) {
		return null;
	}

	@Override
	public Page<ItemQueryDto> findItemsByCategory(CategoryName categoryName, String keyword, PriceFilter priceFilter,
		ItemPage pageDto) {
		return null;
	}

	@Override
	public Page<ItemQueryDto> findByBrand(String keyword, ItemPage searchDto, PriceFilter priceFilter) {
		return null;
	}
}
