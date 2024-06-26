package com.team33.modulecore.item.application;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.team33.modulecore.exception.BusinessLogicException;
import com.team33.modulecore.exception.ExceptionCode;
import com.team33.modulecore.item.domain.entity.Item;
import com.team33.modulecore.item.domain.repository.ItemCommandRepository;
import com.team33.modulecore.review.domain.entity.Review;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Transactional
@Service
public class ItemCommandService {

	private final ItemCommandRepository itemCommandRepository;

	public Item getAndIncreaseView(Long itemId) {
		 itemCommandRepository.incrementView(itemId);

		 return itemCommandRepository.findById(itemId)
			 .orElseThrow(() -> new BusinessLogicException(ExceptionCode.ITEM_NOT_FOUND));
	}

	public void addSales(List<Long> orderedItemsId) {
		orderedItemsId.forEach(itemCommandRepository::incrementSales);
	}

	public void addReviewId(Long itemId, Long reviewId, double star) {
		itemCommandRepository
			.findById(itemId)
			.ifPresent(item -> {
				item.addReviewId(reviewId);
				item.updateCountAndStars(star);
			});
	}

	public void deleteReviewId(Long itemId, Review review) {
		itemCommandRepository
			.findById(itemId)
			.ifPresent(item -> {
					item.deleteReviewId(review.getId());
					item.subtractCountAndStars(review.getStar());
				}
			);
	}

}
