package com.team33.modulecore.cache.dto;

import java.io.Serializable;
import java.util.List;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@ToString
@NoArgsConstructor
@Getter
public class CachedItems<T> implements Serializable {

	private List<T> cachedItems;

	private CachedItems(List<T> cachedItems) {
		this.cachedItems = cachedItems;
	}

	public static <T> CachedItems<T> of(List<T> items) {
		return new CachedItems<>(items);
	}
}
