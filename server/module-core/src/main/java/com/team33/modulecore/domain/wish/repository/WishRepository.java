package com.team33.modulecore.domain.wish.repository;

import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import com.team33.modulecore.domain.item.entity.Item;
import com.team33.modulecore.domain.user.entity.User;
import com.team33.modulecore.domain.wish.entity.Wish;


public interface WishRepository extends JpaRepository<Wish, Long> {


    Wish findByItemAndUser(Item item, User user);


    List<Wish> findAllByItem(long itemId);


    @Query("SELECT sum(w.isWish) from Wish w where w.item.itemId = :itemId")
    int findWishValue(@Param("itemId") long itemId);


    @Query("SELECT w FROM Wish w JOIN Item i ON w.item.itemId = i.itemId where w.user.userId = :userId and w.isWish = 1")
    Page<Wish> findAllByUser(Pageable pageable, @Param("userId") long userId);

    @Query("SELECT i.itemId FROM Wish w JOIN Item i ON w.item.itemId = i.itemId where w.user.userId = :userId and w.isWish = 1")
    List<Long> findItemIdByUser(@Param("userId") long userId);

}
