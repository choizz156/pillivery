package com.team33.modulecore.core.item.application;

import java.util.List;
import java.util.Map;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.team33.modulecore.cache.CacheClient;
import com.team33.modulecore.exception.BusinessLogicException;
import com.team33.modulecore.exception.ExceptionCode;
import com.team33.modulecore.core.item.domain.entity.Item;
import com.team33.modulecore.core.item.domain.repository.ItemCommandRepository;
import com.team33.modulecore.core.review.domain.entity.Review;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Transactional
@Service
public class ItemCommandService {

	private final ItemCommandRepository itemCommandRepository;
	private final CacheClient cacheClient;

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

	@Scheduled(cron = "0 0 2 * * *")
	public void increaseView() {
		Map<String, Long> viewCount = cacheClient.getViewCount();

		viewCount.forEach((key, value) ->
			itemCommandRepository.incrementView(Long.valueOf(key),value)
		);
	}
}
