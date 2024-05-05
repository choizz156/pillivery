package com.team33.modulecore.item.domain.repository;

import com.team33.modulecore.category.domain.CategoryName;
import com.team33.modulecore.item.domain.entity.Item;
import com.team33.modulecore.item.dto.query.PriceFilterDto;
import com.team33.modulecore.item.dto.query.ItemPageDto;
import com.team33.modulecore.item.dto.query.ItemQueryDto;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Repository;

@Repository
public interface ItemQueryRepository  {

    Item findById(long id);

    List<ItemQueryDto> findItemsWithSalesTop9();

    List<ItemQueryDto> findItemsWithDiscountRateTop9();

    Page<ItemQueryDto> findFilteredItems(
        String keyword,
        PriceFilterDto priceFilter,
        ItemPageDto request
    );

    Page<ItemQueryDto> findItemsOnSale(ItemPageDto pageDto);

    Page<ItemQueryDto> findItemsByCategory(CategoryName categoryName,ItemPageDto pageDto);
}
