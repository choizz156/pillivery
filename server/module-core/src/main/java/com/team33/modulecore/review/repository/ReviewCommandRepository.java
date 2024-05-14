package com.team33.modulecore.review.repository;

import java.util.Optional;

import org.springframework.data.repository.Repository;

import com.team33.modulecore.review.domain.entity.Review;

public interface ReviewCommandRepository extends Repository<Review, Long> {

    Review save(Review review);
    Optional<Review> findById(Long reviewId);
    //TODO: 리뷰수 올리는 로직

    // Page<Review> findAllByUser(Pageable pageable, User user); // 마이페이지에서 유저가 작성한 리뷰 목록을 불러옴
    //
    // Page<Review> findAllByItem(Pageable pageable, Item item); // 아이템상세페이지에서 아이템의 리뷰 목록을 불러옴
    //
    // @Query("SELECT avg(r.star) from Review r where r.item.id = :itemId") // 아이템의 리뷰 평점
    // Optional<Double> findReviewAvg(long itemId);
}
