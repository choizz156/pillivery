package com.team33.modulecore.item.domain.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.Repository;
import org.springframework.data.repository.query.Param;

import com.team33.modulecore.item.domain.entity.Item;

public interface ItemCommandRepository extends Repository<Item, Long> {

	Item save(Item item);

	Optional<Item> findById(long id);

	List<Item> saveAll(Iterable<Item> entities);

	@Modifying(clearAutomatically = true)
	@Query("UPDATE Item i SET i.statistics.view = i.statistics.view + 1L WHERE i.id = :itemId")
	Item incrementView(@Param("itemId") long itemId);

	@Modifying(clearAutomatically = true, flushAutomatically = true)
	@Query("UPDATE Item i SET i.statistics.sales = i.statistics.sales + 1L WHERE i.id = :itemId")
	Item incrementSales(@Param("itemId") long itemId);
}
