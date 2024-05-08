package com.team33.modulecore.item.domain.mock;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.persistence.EntityManager;

import com.team33.modulecore.item.domain.entity.Item;
import com.team33.modulecore.item.domain.repository.ItemCommandRepository;

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
	public List<Item> saveAll(Iterable<Item> entities) {
		return List.of();
	}

	@Override
	public Item incrementView(long itemId) {

		em.createQuery("update Item i set i.statistics.view = i.statistics.view + 1L where i.id = :id")
			.setParameter("id", itemId)
			.executeUpdate();

		em.clear(); // update쿼리는 영속성 컨텍스트를 거치지 않으므로 컨텍스트를 비워줘야 다음 조회 때, 영속성 컨텍스트에 엔티티가 업데이트 된다.

		return em.find(Item.class, itemId);
	}
}
