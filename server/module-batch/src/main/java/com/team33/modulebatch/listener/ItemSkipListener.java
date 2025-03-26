package com.team33.modulebatch.listener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.SkipListener;

import com.team33.modulebatch.domain.FailSubscriptionRepository;
import com.team33.modulebatch.domain.entity.FailSubscription;
import com.team33.modulebatch.step.OrderVO;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class ItemSkipListener implements SkipListener<OrderVO, OrderVO> {

	private static final Logger LOGGER = LoggerFactory.getLogger("fileLog");

	private final FailSubscriptionRepository failSubscriptionRepository;

	@Override
	public void onSkipInRead(Throwable t) {
		LOGGER.warn("item reader skip ::: exception = {}", t.getMessage());
	}

	@Override
	public void onSkipInWrite(OrderVO orderVO, Throwable throwable) {
		LOGGER.warn("item writer skip 결제요청 실패 ::: order IdempotencyKey = {}, exception = {}",
			orderVO.getIdempotencyKey(),
			throwable.getMessage()
		);

		FailSubscription entity = FailSubscription.of(orderVO);
		failSubscriptionRepository.save(entity);
	}

	@Override
	public void onSkipInProcess(OrderVO orderVO, Throwable throwable) {

	}
}
