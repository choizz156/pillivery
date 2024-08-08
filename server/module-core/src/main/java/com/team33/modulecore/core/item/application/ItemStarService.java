package com.team33.modulecore.core.item.application;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.team33.modulecore.core.item.domain.entity.Item;

@Transactional(propagation = Propagation.REQUIRES_NEW)
@Service
public class ItemStarService {

	public void updateStarAvg(Item item, double star) {
		item.addCountAndStars(star);
	}

	public void subtractStarAvg(Item item, double star) {
		item.subtractCountAndStars(star);
	}
}
