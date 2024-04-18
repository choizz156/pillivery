package com.team33.modulecore.review.repository;


import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import com.team33.modulecore.item.domain.Item;
import com.team33.modulecore.review.domain.Review;
import com.team33.modulecore.user.domain.User;

public interface ReviewRepository extends JpaRepository<Review, Long> {

    Page<Review> findAllByUser(Pageable pageable, User user); // 마이페이지에서 유저가 작성한 리뷰 목록을 불러옴

    Page<Review> findAllByItem(Pageable pageable, Item item); // 아이템상세페이지에서 아이템의 리뷰 목록을 불러옴

    @Query("SELECT avg(r.star) from Review r where r.item.itemId = :itemId") // 아이템의 리뷰 평점
    Optional<Double> findReviewAvg(long itemId);
}
