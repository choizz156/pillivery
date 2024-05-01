package com.team33.modulecore.wish.application;

import com.team33.modulecore.item.application.ItemService;
import com.team33.modulecore.item.domain.repository.ItemCommandRepository;
import com.team33.modulecore.user.application.UserService;
import com.team33.modulecore.wish.domain.Wish;
import com.team33.modulecore.wish.repository.WishRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class WishService {

    private final ItemService itemService;
    private final ItemCommandRepository itemRepository;
    private final WishRepository wishRepository;
    private final UserService userService;


//    public void refreshWishes(long itemId) {
//        Item item = itemService.findVerifiedItem(itemId);
//        item.setTotalWishes(getWishes(itemId));
//        itemRepository.save(item);
//    }


//    public Wish wishItem(long itemId, int isWish) {
//        User user = userService.getLoginUser();
//
//        Wish wish = wishRepository.findByItemAndUser(itemService.findVerifiedItem(itemId), user);
//
//        if (wish == null) {
//            Wish newWish = new Wish();
//            newWish.addItem(itemService.findVerifiedItem(itemId));
//            newWish.addUser(user);
//            newWish.setIsWish(isWish);
//            wishRepository.save(newWish);
//            refreshWishes(itemId);
//            return newWish;
//        } else {
//            wish.setIsWish(isWish);
//            wishRepository.save(wish);
//            refreshWishes(itemId);
//            return wish;
//        }
//
//    }

    public int getWishes(long itemId) {
        int wishValue = wishRepository.findWishValue(itemId);
        return wishValue;
    }


    public Page<Wish> findWishes(long userId, int page, int size, String sort) {
        return wishRepository.findAllByUser(PageRequest.of(page, size, Sort.by(sort).descending()), userId);
    }

    public Long[] findItemId(long userId) {
        List<Long> itemIdByUser = wishRepository.findItemIdByUser(userId);
        Long[] itemIdList = new Long[itemIdByUser.size()];
        for (int i = 0; i < itemIdByUser.size(); i++) {
            itemIdList[i] = itemIdByUser.get(i);
        }
        return itemIdList;
    }
}
