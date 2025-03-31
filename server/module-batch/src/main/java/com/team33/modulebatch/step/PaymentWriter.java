package com.team33.modulebatch.step;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.ItemWriter;

import com.team33.modulebatch.infra.PaymentApiDispatcher;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class PaymentWriter implements ItemWriter<SubscriptionOrderVO> {

	private static final Logger LOGGER = LoggerFactory.getLogger("fileLog");
	
	private final PaymentApiDispatcher paymentApiDispatcher;

	@Override
	public void write(List<? extends SubscriptionOrderVO> list) {

		if (list.isEmpty()) {
			return;
		}

		try {
			paymentApiDispatcher.dispatch(list);
			LOGGER.info("Payment processed successfully for {} orders", list.size());
		} catch (Exception e) {
			LOGGER.error("Payment processing failed for {} orders: {}", list.size(), e.getMessage());
			throw e; 
		}
	}
}
