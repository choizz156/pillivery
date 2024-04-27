package com.team33.modulecore.item.application;

import com.team33.modulecore.item.domain.entity.Item;
import com.team33.modulecore.item.domain.repository.ItemRepository;
import com.team33.modulecore.item.dto.ItemSearchRequest;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class ItemQueryService {

    private final ItemRepository itemQueryRepository;

    public List<Item> findTop9DiscountItems() {
        return itemQueryRepository.findItemsWithDiscountRateTop9();
    }

    public List<Item> findTop9SaleItems() {
        return itemQueryRepository.findItemsWithSalesTop9();
    }

    public Page<Item> searchItems(String keyword, ItemSearchRequest request) {
        String title = keyword.replace("_", " ");
        return itemQueryRepository.findByTitle(title, request);
    }
}
