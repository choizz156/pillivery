package com.team33.modulecore.cache.dto;

import java.io.Serializable;
import java.util.List;

import com.team33.modulecore.core.item.dto.query.ItemQueryDto;

import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
public class CachedMainItems implements Serializable {

	private List<ItemQueryDto> cachedItems;

	private CachedMainItems(List<ItemQueryDto> cachedItems) {
		this.cachedItems = cachedItems;
	}

	public static CachedMainItems of(List<ItemQueryDto> items) {
		return new CachedMainItems(items);
	}
}
