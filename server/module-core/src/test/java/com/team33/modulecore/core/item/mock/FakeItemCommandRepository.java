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


	@Override
	public void incrementSales(Long itemId) {
		em.createQuery("update Item i set i.statistics.sales = i.statistics.sales + 1 where i.id = :id")
			.setParameter("id", itemId)
			.executeUpdate();

		em.flush();
		em.clear();

	}
}
