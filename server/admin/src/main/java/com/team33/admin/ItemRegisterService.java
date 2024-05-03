package com.team33.moduleapi.admin;

import static java.util.stream.Collectors.groupingBy;

import com.team33.modulecore.category.application.CategoryService;
import com.team33.modulecore.category.domain.Category;
import com.team33.modulecore.category.domain.CategoryName;
import com.team33.modulecore.category.domain.ItemCategory;
import com.team33.modulecore.item.application.BrandService;
import com.team33.modulecore.item.domain.ItemInformation;
import com.team33.modulecore.item.domain.entity.Item;
import com.team33.modulecore.item.domain.repository.ItemCommandRepository;
import com.team33.modulecore.item.domain.repository.ItemQueryRepository;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class ItemRegisterService {

    private final BrandService brandService;
    private final CategoryService categoryService;
    private final ItemQueryRepository itemQueryRepository;
    private final ItemCommandRepository itemCommandRepository;

    public boolean createItem(List<ItemInformation> itemInfo) {

        List<Item> itemList = itemInfo.stream().map(Item::create).collect(Collectors.toList());

        Map<Category, List<Item>> categorizedItems = classifyItemByCategory(itemList);

        for (Map.Entry<Category, List<Item>> entry : categorizedItems.entrySet()) {
            ItemCategory.create(entry.getKey(), entry.getValue());
        }

        return itemCommandRepository.saveAll(itemList);
    }

    private Map<Category, List<Item>> classifyItemByCategory(List<Item> itemList) {
        return itemList.stream()
            .collect(Collectors.groupingBy(
                item -> {
                    CategoryName categoryName = CategoryName.get(
                        item.getItemInformation().getMainFunction()
                    );
                    return categoryService.getCategory(categoryName);
                }
            ));
    }

}
