package com.team33.modulecore.common;

import org.springframework.stereotype.Component;

import com.team33.modulecore.item.domain.entity.Item;
import com.team33.modulecore.item.domain.repository.ItemQueryRepository;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Component
public class ItemFindHelper {

	private final ItemQueryRepository itemQueryRepository;

	public Item findItem(long id) {
		return itemQueryRepository.findById(id);
	}
}
