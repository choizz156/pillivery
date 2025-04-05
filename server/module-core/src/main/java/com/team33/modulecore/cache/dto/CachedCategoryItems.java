package com.team33.modulecore.cache.dto;

import java.io.Serializable;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.support.PageableExecutionUtils;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public class CachedCategoryItems<T> implements Serializable {

	private CachedItems<T> content;
	private long totalElements;
	private int totalPages;
	private int number;
	private int size;

	public CachedCategoryItems(Page<T> items) {
		this.content = CachedItems.of(items.getContent());
		this.totalElements = items.getTotalElements();
		this.totalPages = items.getTotalPages();
		this.number = items.getNumber();
		this.size = items.getSize();
	}

	public Page<T> toPage() {
		return PageableExecutionUtils.getPage(
			content.getCachedItems(),
			PageRequest.of(number, size),
			() -> totalElements
		);
	}

}
