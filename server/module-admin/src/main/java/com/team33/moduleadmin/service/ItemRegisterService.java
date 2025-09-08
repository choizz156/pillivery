package com.team33.moduleadmin.service;

import com.team33.modulecore.cache.CacheType;
import com.team33.modulecore.core.category.domain.CategoryName;
import com.team33.modulecore.core.item.domain.Information;
import com.team33.modulecore.core.item.domain.entity.Item;
import com.team33.modulecore.core.item.domain.repository.ItemCommandRepository;
import com.team33.modulecore.exception.BusinessLogicException;
import com.team33.moduleredis.application.CacheEventPublisher;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@RequiredArgsConstructor
@Service
public class ItemRegisterService {

	private final ItemCommandRepository itemCommandRepository;
	private final CacheEventPublisher cacheEventPublisher;

	public void createItem(List<Information> itemInfo) {

		List<Item> itemList = itemInfo.stream().map(Item::create).collect(Collectors.toList());
		Map<Set<CategoryName>, List<Item>> categorizedItems = classifyItemByCategory(itemList);
		inputCategoryToItem(categorizedItems);
		itemCommandRepository.saveAll(itemList);
	}

	@Transactional
	public void updateItem(long itemId, Information itemInfo) {
		Item item = itemCommandRepository.findById(itemId)
			.orElseThrow(() -> new BusinessLogicException("not found item with id " + itemId));

		item.chageInformation(itemInfo);

		item.getItemCategory().forEach( i -> cacheEventPublisher.publish(CacheType.CATEGORY_ITEMS.name(), i.name()));
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
