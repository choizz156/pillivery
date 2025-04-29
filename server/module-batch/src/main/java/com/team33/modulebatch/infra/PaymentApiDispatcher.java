package com.team33.modulebatch.infra;

import java.time.Duration;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.team33.modulebatch.step.SubscriptionOrderVO;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class PaymentApiDispatcher {


	private static final String HOST = "www.pv-alb.r-e.kr:8080";
	private static final String URL = HOST + "/api/payments/approve/subscriptions/";

	private final RestTemplateSender restTemplateSender;

	private final Cache<String, String> idempotencyCache = Caffeine.newBuilder()
		.maximumSize(1000)
		.expireAfterWrite(Duration.ofHours(12L))
		.build();

	public void dispatch(List<? extends SubscriptionOrderVO> list){

		List<SubscriptionOrderVO> orderList = filterProcessedOrders(list);

		orderList.forEach(order -> {
			send(order.getSubscriptionOrderId());
			idempotencyCache.put(order.getIdempotencyKey(), "item write -> api process");
		});
	}

	private List<SubscriptionOrderVO> filterProcessedOrders(List<? extends SubscriptionOrderVO> orders) {

		return orders.stream()
			.filter(this::isNotProcessed)
			.collect(Collectors.toUnmodifiableList());
	}

	private boolean isNotProcessed(SubscriptionOrderVO order) {

		return idempotencyCache.getIfPresent(order.getIdempotencyKey()) == null;
	}

	private void send(long subscriptionOrderId) {

		restTemplateSender.sendToPost(
			"",
			URL + subscriptionOrderId,
			null,
			String.class
		);
	}
}
