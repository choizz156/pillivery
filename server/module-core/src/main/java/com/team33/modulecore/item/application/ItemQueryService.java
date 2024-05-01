package com.team33.modulecore.item.application;

import com.team33.modulecore.item.domain.entity.Item;
import com.team33.modulecore.item.domain.repository.ItemQueryRepository;
import com.team33.modulecore.item.dto.ItemResponseDto;
import com.team33.modulecore.item.dto.ItemSearchRequest;
import com.team33.modulecore.item.dto.PriceFilterDto;
import com.team33.modulecore.item.dto.query.ItemQueryDto;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class ItemQueryService {

    private final ItemQueryRepository itemQueryRepository;

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

    public Page<ItemResponseDto> findFilteredItemByPrice(
        PriceFilterDto priceFilterDto,
        ItemSearchRequest itemSearchRequest
    ) {
        Page<ItemQueryDto> itemsByPrice =
            itemQueryRepository.findItemsByPrice(priceFilterDto, itemSearchRequest);

        List<ItemResponseDto> itemResponseDtos = itemsByPrice.getContent().stream()
            .map(ItemResponseDto::from)
            .collect(Collectors.toUnmodifiableList());

        return new PageImpl<>(
            itemResponseDtos,
            PageRequest.of(itemsByPrice.getNumber() - 1, itemsByPrice.getSize(),
                itemsByPrice.getSort()),
            itemsByPrice.getTotalElements()
        );
    }
}
