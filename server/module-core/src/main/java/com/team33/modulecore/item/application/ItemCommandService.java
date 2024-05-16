package com.team33.modulecore.item.application;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.team33.modulecore.item.domain.entity.Item;
import com.team33.modulecore.item.domain.repository.ItemCommandRepository;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Transactional
@Service
public class ItemCommandService {

    private final ItemCommandRepository itemCommandRepository;

    public Item increaseView(long itemId) {
       return itemCommandRepository.incrementView(itemId);
    }

	public void addSales(List<Item> orderedItems) {
		orderedItems.forEach(Item::addSales);
	}
}
