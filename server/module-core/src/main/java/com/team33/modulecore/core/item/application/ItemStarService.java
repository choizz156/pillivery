package com.team33.modulecore.core.item.application;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.team33.modulecore.core.item.domain.entity.Item;
import com.team33.moduleredis.domain.annotation.DistributedLock;


@Transactional(propagation = Propagation.REQUIRES_NEW)
@Service
public class ItemStarService {

	@DistributedLock(key = "'item:star:' + #item.id")
	public void updateStarAvg(Item item, double star) {
		item.addCountAndStars(star);
	}

	@DistributedLock(key = "'item:star:' + #item.id")
	public void subtractStarAvg(Item item, double star) {
		item.subtractCountAndStars(star);
	}
}
