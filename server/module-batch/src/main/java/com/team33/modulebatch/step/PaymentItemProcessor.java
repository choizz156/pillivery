package com.team33.modulebatch.step;

import org.springframework.batch.item.ItemProcessor;
import org.springframework.stereotype.Component;

import lombok.NoArgsConstructor;

@NoArgsConstructor
@Component
public class PaymentItemProcessor implements ItemProcessor<SubscriptionOrderVO, SubscriptionOrderVO> {

	private Long jobId;

	@Override
	public SubscriptionOrderVO process(SubscriptionOrderVO subscriptionOrderVO) {
		if (jobId == null) {
			throw new IllegalStateException("JobId가 설정돼있어야 합니다.");
		}

		String idempotencyKey = jobId + "_" + subscriptionOrderVO.getSubscriptionOrderId() + "_" + subscriptionOrderVO.getNextPaymentDate();
		subscriptionOrderVO.setIdempotencyKey(idempotencyKey);

		return subscriptionOrderVO;
	}

	public void initialize(Long jobId) {
		this.jobId = jobId;
	}
}
