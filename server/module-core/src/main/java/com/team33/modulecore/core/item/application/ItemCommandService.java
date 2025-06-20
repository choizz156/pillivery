package com.team33.modulecore.core.item.application;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.team33.modulecore.core.item.domain.repository.ItemCommandRepository;
import com.team33.modulecore.core.review.domain.entity.Review;
import com.team33.modulecore.exception.BusinessLogicException;
import com.team33.modulecore.exception.ExceptionCode;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Transactional
@Service
public class ItemCommandService {

	private final ItemCommandRepository itemCommandRepository;
	private final ItemStarService itemStarService;

	public void addReviewId(Long itemId, Long reviewId, double star) {
		itemCommandRepository
			.findById(itemId)
			.ifPresentOrElse(item -> {
				itemStarService.updateStarAvg(item, star);
				item.addReviewId(reviewId);
			}, () -> {
				throw new BusinessLogicException(ExceptionCode.ITEM_NOT_FOUND);
			});
	}

	public void deleteReviewId(Long itemId, Review review) {
		itemCommandRepository
			.findById(itemId)
			.ifPresentOrElse(item -> {
				itemStarService.subtractStarAvg(item, review.getStar());
				item.deleteReviewId(review.getId());
			}, () -> {
				throw new BusinessLogicException(ExceptionCode.ITEM_NOT_FOUND);
			});
	}
}
