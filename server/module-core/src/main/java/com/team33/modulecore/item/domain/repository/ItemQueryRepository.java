package com.team33.modulecore.item.domain.repository;

import com.team33.modulecore.item.domain.entity.Item;
import com.team33.modulecore.item.dto.ItemSearchRequest;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.repository.Repository;


public interface ItemQueryRepository extends Repository<Item, Long> {

    Page<Item> findByTitle(String title, ItemSearchRequest pageable);
    List<Item> findItemsWithSalesTop9();
    List<Item> findItemsWithDiscountRateTop9();

}
