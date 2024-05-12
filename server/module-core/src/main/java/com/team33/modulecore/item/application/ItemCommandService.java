package com.team33.modulecore.item.application;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.team33.modulecore.item.domain.entity.Item;
import com.team33.modulecore.item.domain.repository.ItemCommandRepository;
import com.team33.modulecore.item.domain.repository.ItemQueryRepository;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Transactional
@Service
public class ItemCommandService {

    private final ItemQueryRepository itemQueryRepository;
    private final ItemCommandRepository itemCommandRepository;

    public Item increaseView(long itemId) {
       return itemCommandRepository.incrementView(itemId);
    }
}
