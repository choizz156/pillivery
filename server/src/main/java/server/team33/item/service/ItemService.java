package server.team33.item.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import server.team33.item.entity.Item;
import server.team33.item.repository.ItemRepository;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ItemService {

    private final ItemRepository itemRepository;


    public Item createItem(Item item) {

        return itemRepository.save(item);
    }

    // 아이템 상세페이지 조회 비지니스 로직
    public Item findItem(long itemId) {
        Item item = findVerifiedItem(itemId);
        return itemRepository.save(item);
    }

    public Item findVerifiedItem(long itemId) {
        Optional<Item> item = itemRepository.findById(itemId);
        Item findItem = item.orElseThrow(() -> new RuntimeException());
        return findItem;
    }

    public Page<Item> findItems(String categoryName, int page, int size, String sort) {
        Page<Item> findItems = itemRepository.findAllByCategoryName(
                PageRequest.of(page, size, Sort.by(sort).descending()), categoryName);
        return findItems;
    }


}
