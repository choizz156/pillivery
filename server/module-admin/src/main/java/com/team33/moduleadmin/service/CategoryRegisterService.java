package com.team33.moduleadmin.service;

import org.springframework.stereotype.Service;

import com.team33.modulecore.core.category.domain.Category;
import com.team33.modulecore.core.category.domain.CategoryName;
import com.team33.modulecore.core.category.domain.CategoryRepository;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class CategoryRegisterService {

	private final CategoryRepository categoryRepository;

	public Long save(CategoryName categoryName) {
		Category byCategoryName = categoryRepository.findByCategoryName(categoryName);
		if (byCategoryName == null) {
			Category category = categoryRepository.save(Category.of(categoryName));
			return category.getId();
		}
		return byCategoryName.getId();
	}
}
