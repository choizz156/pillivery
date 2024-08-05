package com.team33.modulecore.cache;

import java.io.Serializable;
import java.util.List;

import com.team33.modulecore.core.item.dto.query.ItemQueryDto;

import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
public class CachedItems implements Serializable {

	private List<ItemQueryDto> mainItems;

	private CachedItems(List<ItemQueryDto> mainItems) {
		this.mainItems = mainItems;
	}

	public static CachedItems of(List<ItemQueryDto> items) {
		return new CachedItems(items);
	}
}
