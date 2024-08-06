package com.team33.modulecore.cache;

import java.io.Serializable;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.team33.modulecore.core.item.dto.query.ItemQueryDto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public class CachedMainItems implements Serializable {

	private static final Logger log = LoggerFactory.getLogger(CachedMainItems.class);

	private List<ItemQueryDto> cachedItems;

	private CachedMainItems(List<ItemQueryDto> cachedItems) {
		this.cachedItems = cachedItems;
	}

	public static CachedMainItems of(List<ItemQueryDto> items) {
		return new CachedMainItems(items);
	}
}
