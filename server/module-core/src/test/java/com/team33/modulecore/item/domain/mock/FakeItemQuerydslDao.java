package com.team33.modulecore.item.domain.mock;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import com.team33.modulecore.category.domain.CategoryName;
import com.team33.modulecore.item.domain.entity.Item;
import com.team33.modulecore.item.domain.repository.ItemQueryRepository;
import com.team33.modulecore.item.dto.query.ItemPageDto;
import com.team33.modulecore.item.dto.query.ItemQueryDto;
import com.team33.modulecore.item.dto.query.PriceFilterDto;

public class FakeItemQuerydslDao implements ItemQueryRepository {

    private Map<Long, ItemQueryDto> store;

    public FakeItemQuerydslDao() {
        this.store = new HashMap<>();
        store.put(1L, new ItemQueryDto().builder()
            .productName("test")
            .build());
    }

    @Override
    public Item findById(long id) {
        return null;
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
    public Page<ItemQueryDto> findFilteredItems(
        String keyword,
        PriceFilterDto priceFilter,
        ItemPageDto request
    ) {
        return new PageImpl<>(List.of(store.get(1L)), PageRequest.of(1, 10), store.size());
    }

    @Override
    public Page<ItemQueryDto> findItemsOnSale(String keyword, PriceFilterDto priceFilterDto, ItemPageDto pageDto) {
        return null;
    }

    @Override
    public Page<ItemQueryDto> findItemsByCategory(CategoryName categoryName, String keyword,
        PriceFilterDto priceFilterDto, ItemPageDto pageDto) {
        return null;
    }
}
