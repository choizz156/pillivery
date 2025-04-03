package com.team33.modulebatch.step;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.ItemWriter;
import org.springframework.stereotype.Component;

import com.team33.modulebatch.exception.SubscriptionFailException;
import com.team33.modulebatch.infra.PaymentApiDispatcher;
import com.team33.modulecore.core.order.application.SubscriptionOrderService;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Component
public class PaymentWriter implements ItemWriter<SubscriptionOrderVO> {

	private static final Logger LOGGER = LoggerFactory.getLogger("fileLog");
	
	private PaymentApiDispatcher paymentApiDispatcher;
	private SubscriptionOrderService subscriptionOrderService;

	@Override
	public void write(List<? extends SubscriptionOrderVO> list) {

		if (list.isEmpty()) {
			return;
		}

		try {
			paymentApiDispatcher.dispatch(list);
			LOGGER.info("Payment processed successfully for {} orders", list.size());
		} catch (SubscriptionFailException e) {
			long subscriptionOrderId = e.getSubscriptionOrderId();
			subscriptionOrderService.updateOrderStatus(subscriptionOrderId);
		}
	}


	public void initialize(PaymentApiDispatcher paymentApiDispatcher, SubscriptionOrderService subscriptionOrderService) {
		this.paymentApiDispatcher = paymentApiDispatcher;
		this.subscriptionOrderService = subscriptionOrderService;
	}
}
