package com.team33.modulebatch.listener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.SkipListener;

import com.team33.modulebatch.domain.ErrorItemRepository;
import com.team33.modulebatch.domain.entity.ErrorItem;
import com.team33.modulebatch.step.SubscriptionOrderVO;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class ItemSkipListener implements SkipListener<SubscriptionOrderVO, SubscriptionOrderVO> {

	private static final Logger LOGGER = LoggerFactory.getLogger("fileLog");

	private final ErrorItemRepository errorItemRepository;

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

		ErrorItem entity = ErrorItem.byServerError(subscriptionOrderVO);
		errorItemRepository.save(entity);
	}

	@Override
	public void onSkipInProcess(SubscriptionOrderVO subscriptionOrderVO, Throwable throwable) {

	}
}
