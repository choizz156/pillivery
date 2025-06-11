package com.team33.modulecore.cache;

import org.springframework.data.domain.Page;

import com.team33.modulecore.core.category.domain.CategoryName;
import com.team33.modulecore.core.item.domain.ItemSortOption;
import com.team33.modulecore.core.item.dto.query.ItemPage;

public interface CacheStrategy<T> {

	Page<T> getCachedItems(CategoryName categoryName, ItemPage itemPage);

	boolean supports(ItemSortOption itemSortOption);
}
