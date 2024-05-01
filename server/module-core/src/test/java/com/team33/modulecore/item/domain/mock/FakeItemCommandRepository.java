package com.team33.modulecore.item.domain.mock;

import com.team33.modulecore.item.domain.entity.Item;
import com.team33.modulecore.item.domain.repository.ItemCommandRepository;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;


public class FakeItemCommandRepository implements ItemCommandRepository {

    private Map<Long, Item> items;

    public FakeItemCommandRepository() {
        this.items = new HashMap<>();
    }

    @Override
    public Item save(Item item) {
        items.put(item.getId(), item);
        return item;
    }

    @Override
    public Optional<Item> findById(long id) {
        return items.containsKey(id) ? Optional.of(items.get(id)) : Optional.empty();
    }
}
