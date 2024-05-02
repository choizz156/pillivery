package com.team33.modulecore.item.domain.repository;

import com.team33.modulecore.item.domain.entity.Item;
import com.team33.modulecore.item.dto.ItemPageDto;
import com.team33.modulecore.item.dto.PriceFilterDto;
import com.team33.modulecore.item.dto.query.ItemQueryDto;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Repository;

@Repository
public interface ItemQueryRepository {

    List<Item> findItemsWithSalesTop9();

    List<Item> findItemsWithDiscountRateTop9();

    Page<ItemQueryDto> findFilteredItems(
        String keyword,
        PriceFilterDto priceFilter,
        ItemPageDto request
    );

    Page<ItemQueryDto> findItemOnSale(ItemPageDto pageDto);
}
