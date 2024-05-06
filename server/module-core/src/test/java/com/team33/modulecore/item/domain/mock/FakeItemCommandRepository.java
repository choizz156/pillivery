package com.team33.modulecore.item.domain.mock;

import com.team33.modulecore.FixtureMonkeyFactory;
import com.team33.modulecore.item.domain.entity.Item;
import com.team33.modulecore.item.domain.repository.ItemCommandRepository;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;


public class FakeItemCommandRepository implements ItemCommandRepository {

    private Map<Long, Item> items;

    public FakeItemCommandRepository() {
        this.items = new HashMap<>();
        Item item = FixtureMonkeyFactory.get().giveMeBuilder(Item.class)
            .set("id", 1L)
            .sample();
        this.items.put(item.getId(), item);
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

    @Override
    public <S extends Item> List<S> saveAll(Iterable<S> entities) {
        return List.of();
    }
}
