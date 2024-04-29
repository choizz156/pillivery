package com.team33.modulecore.item.domain.mock;

import com.team33.modulecore.item.domain.entity.Item;
import com.team33.modulecore.item.domain.repository.ItemRepository;
import java.util.List;
import java.util.Optional;
import javax.persistence.EntityManager;


public class TestItemRepository implements ItemRepository {

    private EntityManager entityManager;

    public TestItemRepository(EntityManager entityManager) {
        this.entityManager = entityManager;
    }


    @Override
    public <S extends Item> List<S> saveAll(Iterable<S> entities) {
        return List.of();
    }

    @Override
    public Item save(Item item) {
        entityManager.persist(item);
        return item;
    }

    @Override
    public Optional<Item> findById(long id) {
        Item item = entityManager.find(Item.class, id);
        return Optional.ofNullable(item);
    }
}
