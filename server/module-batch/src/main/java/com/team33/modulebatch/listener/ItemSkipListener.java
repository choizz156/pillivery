package com.team33.modulebatch.listener;

import java.time.LocalDate;
import java.time.ZoneId;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.SkipListener;

import com.team33.modulebatch.domain.DelayedItemRepository;
import com.team33.modulebatch.domain.entity.DelayedItem;
import com.team33.modulebatch.step.SubscriptionOrderVO;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class ItemSkipListener implements SkipListener<SubscriptionOrderVO, SubscriptionOrderVO> {

	private static final Logger LOGGER = LoggerFactory.getLogger("fileLog");

	private final DelayedItemRepository delayedItemRepository;

	@Override
	public void onSkipInRead(Throwable t) {

		LOGGER.error("item reader skip ::: exception = {}", t.getMessage());
	}

	@Override
	public void onSkipInWrite(SubscriptionOrderVO subscriptionOrderVO, Throwable throwable) {

		LOGGER.warn("item writer skip 결제요청 실패 ::: order IdempotencyKey = {}, exception = {}",
			subscriptionOrderVO.getIdempotencyKey(),
			throwable.getMessage()
		);

		DelayedItem entity = DelayedItem.of(
			subscriptionOrderVO.getSubscriptionOrderId(),
			throwable.getLocalizedMessage(),
			LocalDate.now(ZoneId.of("Asia/Seoul")
			));
		delayedItemRepository.save(entity);
	}

	@Override
	public void onSkipInProcess(SubscriptionOrderVO subscriptionOrderVO, Throwable throwable) {

	}
}
