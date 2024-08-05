package com.team33.modulecore.core.common;

import org.springframework.stereotype.Component;

import com.team33.modulecore.exception.BusinessLogicException;
import com.team33.modulecore.exception.ExceptionCode;
import com.team33.modulecore.core.item.domain.entity.Item;
import com.team33.modulecore.core.item.domain.repository.ItemCommandRepository;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Component
public class ItemFindHelper {

	private final ItemCommandRepository itemCommandRepository;

	public Item findItem(long id) {
		return itemCommandRepository.findById(id)
			.orElseThrow(() -> new BusinessLogicException(ExceptionCode.ITEM_NOT_FOUND));
	}
}
