package com.team33.modulecore.item.domain.repository;

import com.team33.modulecore.item.domain.entity.Item;
import com.team33.modulecore.item.dto.ItemSearchRequest;
import com.team33.modulecore.item.dto.PriceFilterDto;
import com.team33.modulecore.item.dto.query.ItemQueryDto;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Repository;

@Repository
public interface ItemQueryRepository  {

    Page<Item> findByTitle(String title, ItemSearchRequest request);
    List<Item> findItemsWithSalesTop9();
    List<Item> findItemsWithDiscountRateTop9();
    Page<ItemQueryDto> findItemsByPrice(PriceFilterDto priceFilter, ItemSearchRequest request);

}
