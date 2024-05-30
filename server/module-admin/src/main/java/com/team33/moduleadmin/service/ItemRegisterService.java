package com.team33.moduleadmin.service;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.team33.modulecore.category.domain.CategoryName;
import com.team33.modulecore.item.domain.Information;
import com.team33.modulecore.item.domain.entity.Item;
import com.team33.modulecore.item.domain.repository.ItemCommandRepository;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class ItemRegisterService {

	private final ItemCommandRepository itemCommandRepository;

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
				item.getItemCategory().addAll(entry.getKey());
				item.addIncludedCategory(entry.getKey());
			});
		}
	}
}
