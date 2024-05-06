package com.team33.modulecore.item.domain.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.repository.Repository;

import com.team33.modulecore.item.domain.entity.Item;

public interface ItemCommandRepository extends Repository<Item, Long> {

	Item save(Item item);

	Optional<Item> findById(long id);

	List<Item> saveAll(Iterable<Item> entities);
}
