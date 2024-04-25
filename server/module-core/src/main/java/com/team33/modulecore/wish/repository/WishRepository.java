package com.team33.modulecore.wish.repository;

import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import com.team33.modulecore.item.domain.Item;
import com.team33.modulecore.user.domain.User;
import com.team33.modulecore.wish.domain.Wish;


public interface WishRepository extends JpaRepository<Wish, Long> {


    Wish findByItemAndUser(Item item, User user);

    @Query("SELECT sum(w.isWish) from Wish w where w.item.id = :itemId")
    int findWishValue(@Param("itemId") long itemId);

    @Query("SELECT w FROM Wish w JOIN Item i ON w.item.id = i.id where w.user.id = :userId and w.isWish = 1")
    Page<Wish> findAllByUser(Pageable pageable, @Param("userId") long userId);

    @Query("SELECT i.id FROM Wish w JOIN Item i ON w.item.id = i.id where w.user.id = :userId and w.isWish = 1")
    List<Long> findItemIdByUser(@Param("userId") long userId);

}
