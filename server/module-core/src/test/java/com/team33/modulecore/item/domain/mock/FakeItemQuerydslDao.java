package com.team33.modulecore.item.domain.mock;

import com.team33.modulecore.item.domain.Brand;
import com.team33.modulecore.item.domain.entity.Item;
import com.team33.modulecore.item.domain.repository.ItemQueryRepository;
import com.team33.modulecore.item.dto.ItemPageDto;
import com.team33.modulecore.item.dto.PriceFilterDto;
import com.team33.modulecore.item.dto.query.ItemQueryDto;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

public class FakeItemQuerydslDao implements ItemQueryRepository {

    private Map<Long, ItemQueryDto> store;

    public FakeItemQuerydslDao() {
        this.store = new HashMap<>();
        store.put(1L, new ItemQueryDto().builder()
            .title("title")
            .brand(Brand.MYNI)
            .nutritionFacts(new ArrayList<>())
            .build());
    }

    @Override
    public List<Item> findItemsWithSalesTop9() {
        return List.of();
    }

    @Override
    public List<Item> findItemsWithDiscountRateTop9() {
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
    public Page<ItemQueryDto> findItemOnSale(ItemPageDto pageDto) {
        return new PageImpl<>(List.of(store.get(1L)), PageRequest.of(1, 10), store.size());
    }
}
