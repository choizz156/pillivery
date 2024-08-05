package com.team33.moduleadmin.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.team33.modulecore.core.item.domain.entity.Item;
import com.team33.modulecore.core.item.domain.repository.ItemCommandRepository;


public class FakeItemCommandRepository implements ItemCommandRepository {

	private final List<Item> items;

	public FakeItemCommandRepository() {
		this.items = new ArrayList<>();
	}

	@Override
	public Item save(Item item) {
		items.add(item);
		return item;
	}

	@Override
	public Optional<Item> findById(long id) {
		return Optional.ofNullable(items.get((int)id));
	}

	@Override
	public void saveAll(final Iterable<Item> entities) {
		final List<Item> items = new ArrayList<>();
		for (Item entity : entities) {
			items.add(save(entity));
		}
	}

	@Override
	public void incrementView(Long itemId) {
	}

	@Override
	public int incrementSales(Long itemId) {
		return 0;
	}
}
