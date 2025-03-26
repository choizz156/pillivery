package com.team33.modulebatch.step;

import org.springframework.batch.item.ItemProcessor;

import lombok.NoArgsConstructor;

@NoArgsConstructor
public class PaymentItemProcessor implements ItemProcessor<OrderVO, OrderVO> {

	private Long jobId;

	public PaymentItemProcessor(Long jobId) {
		this.jobId = jobId;
	}

	@Override
	public OrderVO process(OrderVO orderVO) {

		String idempotencyKey = jobId + "_" + orderVO.getOrderId() + "_" + orderVO.getNextPaymentDate();
		orderVO.setIdempotencyKey(idempotencyKey);

		return orderVO;
	}
}
