package com.team33.modulecore.core.item.domain.repository;

import java.util.Optional;

import org.springframework.data.repository.Repository;

import com.team33.modulecore.core.item.domain.entity.Item;

/**
 * The interface Item command repository.
 */
public interface ItemCommandRepository extends Repository<Item, Long> {

	Item save(Item item);

	Optional<Item> findById(long id);

	void saveAll(Iterable<Item> entities);

}

