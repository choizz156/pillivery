package com.team33.modulecore.cache;

import java.io.Serializable;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.support.PageableExecutionUtils;

import com.team33.modulecore.core.item.dto.query.ItemQueryDto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public class CachedCategoryItems<T> implements Serializable {

	private static final Logger log = LoggerFactory.getLogger(CachedCategoryItems.class);

	private List<T> content;
	private long totalElements;
	private int totalPages;
	private int number;
	private int size;

	public CachedCategoryItems(Page<ItemQueryDto> cachedItems) {
		this.content = (List<T>)cachedItems.getContent();
		this.totalElements = cachedItems.getTotalElements();
		this.totalPages = cachedItems.getTotalPages();
		this.number = cachedItems.getNumber();
		this.size = cachedItems.getSize();
	}

	public Page<T> toPage() {
		return PageableExecutionUtils.getPage(
			content,
			PageRequest.of(number, size),
			() -> (long)totalPages
		);
	}

}
