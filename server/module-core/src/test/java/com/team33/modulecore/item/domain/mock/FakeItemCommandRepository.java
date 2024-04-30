package com.team33.modulecore.item.domain.mock;

import com.team33.modulecore.item.domain.entity.Item;
import com.team33.modulecore.item.domain.repository.ItemCommandRepository;
import java.util.Optional;
import javax.persistence.EntityManager;


public class FakeItemCommandRepository implements ItemCommandRepository {

    private final EntityManager entityManager;

    public FakeItemCommandRepository(EntityManager entityManager) {
        this.entityManager = entityManager;
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
