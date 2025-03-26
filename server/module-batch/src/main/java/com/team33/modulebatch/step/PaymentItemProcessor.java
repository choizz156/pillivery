package com.team33.modulebatch.step;

import org.springframework.batch.item.ItemProcessor;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class PaymentItemProcessor implements ItemProcessor<OrderVO, OrderVO> {

	private final Long jobId;

	@Override
	public OrderVO process(OrderVO orderVO){

		String idempotencyKey = jobId + "_" + orderVO.getOrderId() + "_" + orderVO.getNextPaymentDate();
		orderVO.setIdempotencyKey(idempotencyKey);

		return orderVO;
	}
}
