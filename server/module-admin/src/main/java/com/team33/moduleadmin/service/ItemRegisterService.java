package com.team33.moduleadmin.service;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.team33.modulecore.core.category.domain.CategoryName;
import com.team33.modulecore.core.item.domain.Information;
import com.team33.modulecore.core.item.domain.entity.Item;
import com.team33.modulecore.core.item.domain.repository.ItemCommandRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Service
public class ItemRegisterService {

	private final ItemCommandRepository itemCommandRepository;
	private final CategoryRegisterService categoryRegisterService;

	public void createItem(List<Information> itemInfo) {

		List<Item> itemList = itemInfo.stream().map(Item::create).collect(Collectors.toList());
		Map<Set<CategoryName>, List<Item>> categorizedItems = classifyItemByCategory(itemList);
		inputCategoryToItem(categorizedItems);
		itemCommandRepository.saveAll(itemList);
	}

	private Map<Set<CategoryName>, List<Item>> classifyItemByCategory(List<Item> itemList) {
		return itemList.stream()
			.collect(Collectors.groupingBy(item -> CategoryName.classify(item.getInformation().getMainFunction())));
	}

	private void inputCategoryToItem(final Map<Set<CategoryName>, List<Item>> categorizedItems) {
		for (Map.Entry<Set<CategoryName>, List<Item>> entry : categorizedItems.entrySet()) {
			entry.getValue().forEach(item -> {
				for (CategoryName categoryName : entry.getKey()) {
					item.getItemCategory().add(categoryName);
				}
				item.addIncludedCategories(entry.getKey());
			});
		}
	}
}
