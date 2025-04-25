package com.team33.modulecore.core.item.mock;

import java.util.Map;
import java.util.Optional;

import javax.persistence.EntityManager;

import com.team33.modulecore.core.item.domain.entity.Item;
import com.team33.modulecore.core.item.domain.repository.ItemCommandRepository;

public class FakeItemCommandRepository implements ItemCommandRepository {

	private Map<Long, Item> items;
	private EntityManager em;

	public FakeItemCommandRepository(EntityManager em) {
		this.em = em;
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
	public void saveAll(Iterable<Item> entities) {
	}

}
