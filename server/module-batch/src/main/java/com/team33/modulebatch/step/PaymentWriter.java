package com.team33.modulebatch.step;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.ItemWriter;
import org.springframework.stereotype.Component;

import com.team33.modulebatch.infra.PaymentApiDispatcher;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Component
public class PaymentWriter implements ItemWriter<SubscriptionOrderVO> {

	private static final Logger LOGGER = LoggerFactory.getLogger("fileLog");

	private PaymentApiDispatcher paymentApiDispatcher;

	@Override
	public void write(List<? extends SubscriptionOrderVO> list) {

		if (list.isEmpty()) {
			return;
		}

		try {
			paymentApiDispatcher.dispatch(list);
			LOGGER.info("Payment processed successfully for {} orders", list.size());
		} catch (Exception e) {
			LOGGER.info("Payment processed failed for {} orders, message = {}", list.size(), e.getMessage());
		}
	}


	public void initialize(PaymentApiDispatcher paymentApiDispatcher) {
		this.paymentApiDispatcher = paymentApiDispatcher;
	}
}
