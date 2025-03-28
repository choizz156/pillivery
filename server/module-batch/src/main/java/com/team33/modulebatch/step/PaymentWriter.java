package com.team33.modulebatch.step;

import java.util.List;

import org.springframework.batch.item.ItemWriter;

import com.team33.modulebatch.infra.PaymentApiDispatcher;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class PaymentWriter implements ItemWriter<SubscriptionOrderVO> {

	private final PaymentApiDispatcher paymentApiDispatcher;

	@Override
	public void write(List<? extends SubscriptionOrderVO> list) {

		if (list.isEmpty()) {
			return;
		}

		paymentApiDispatcher.dispatch(list);
	}
}
