package com.team33.moduleadmin.service;

import org.springframework.stereotype.Service;

import com.team33.modulecore.category.domain.Category;
import com.team33.modulecore.category.domain.CategoryName;
import com.team33.modulecore.category.domain.CategoryRepository;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class CategoryRegisterService {

	private final CategoryRepository categoryRepository;

	public void save(CategoryName categoryName) {
		categoryRepository.save(Category.of(categoryName));
	}
}
