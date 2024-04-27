package com.team33.modulecore.item.domain.repository;

import com.team33.modulecore.item.domain.Item;
import com.team33.modulecore.item.dto.ItemSearchRequest;
import org.springframework.data.domain.Page;

public interface ItemQueryRepository {

    Page<Item> findByTitle(String title, ItemSearchRequest pageable);
}
