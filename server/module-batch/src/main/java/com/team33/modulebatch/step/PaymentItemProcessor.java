package com.team33.modulebatch.step;

import org.springframework.batch.item.ItemProcessor;

import lombok.NoArgsConstructor;

@NoArgsConstructor
public class PaymentItemProcessor implements ItemProcessor<SubscriptionOrderVO, SubscriptionOrderVO> {

	private Long jobId;

	public PaymentItemProcessor(Long jobId) {
		this.jobId = jobId;
	}

	@Override
	public SubscriptionOrderVO process(SubscriptionOrderVO orderVO) {

		String idempotencyKey = jobId + "_" + orderVO.getSubscriptionOrderId() + "_" + orderVO.getNextPaymentDate();
		orderVO.setIdempotencyKey(idempotencyKey);

		return orderVO;
	}
}
