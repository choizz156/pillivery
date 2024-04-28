package com.team33.modulecore.item.domain.repository;

import com.team33.modulecore.item.domain.entity.Item;
import java.util.Optional;
import org.springframework.data.repository.Repository;

public interface ItemCommandRepository extends Repository<Item, Long> {

    Item save(Item item);
    Optional<Item> findById(long id);
}
