package com.team33.modulecore.item.application;

import com.team33.modulecore.item.domain.repository.ItemQueryRepository;
import com.team33.modulecore.item.dto.ItemPageDto;
import com.team33.modulecore.item.dto.ItemResponseDto;
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

    public List<ItemQueryDto> findTop9DiscountItems() {
        return itemQueryRepository.findItemsWithDiscountRateTop9();
    }

    public List<ItemQueryDto> findTop9SaleItems() {
        return itemQueryRepository.findItemsWithSalesTop9();
    }

    public Page<ItemResponseDto> findFilteredItem(
        String keyword,
        PriceFilterDto priceFilterDto,
        ItemPageDto itemPageDto
    ) {
        Page<ItemQueryDto> itemsByPrice =
            itemQueryRepository.findFilteredItems(keyword, priceFilterDto, itemPageDto);

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

    public Page<ItemResponseDto> findItemOnSale(ItemPageDto pageDto) {
        Page<ItemQueryDto> itemOnSale = itemQueryRepository.findItemOnSale(pageDto);

        List<ItemResponseDto> itemResponseDtos = itemOnSale.getContent().stream()
            .map(ItemResponseDto::from)
            .collect(Collectors.toUnmodifiableList());

        return new PageImpl<>(
            itemResponseDtos,
            PageRequest.of(itemOnSale.getNumber() - 1, itemOnSale.getSize(), itemOnSale.getSort()),
            itemOnSale.getTotalElements()
        );

    }
}
