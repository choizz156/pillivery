package com.team33.modulebatch.infra;

import java.time.Duration;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.team33.modulebatch.step.SubscriptionOrderVO;
import com.team33.moduleexternalapi.infra.RestTemplateSender;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class PaymentApiDispatcher {

	private static final String HOST = "http://localhost:8080";
	private static final String URL = HOST + "/api/payments/approve/subscriptions/";

	private final RestTemplateSender restTemplateSender;

	Cache<String, String> idempotencyCache = Caffeine.newBuilder()
		.maximumSize(1000)
		.expireAfterWrite(Duration.ofHours(12L))
		.build();

	public void dispatch(List<? extends SubscriptionOrderVO> list) {
		List<? extends SubscriptionOrderVO> orderList = checkDuplicationKeyInCache(list);

		orderList.forEach(order -> {
			send(order.getSubscriptionOrderId());
			idempotencyCache.put(order.getIdempotencyKey(), "finished");
		});
	}

	private List<? extends SubscriptionOrderVO> checkDuplicationKeyInCache(List<? extends SubscriptionOrderVO> list) {
		return list.stream()
			.filter(order -> idempotencyCache.getIfPresent(order.getIdempotencyKey()) == null)
			.collect(Collectors.toUnmodifiableList());
	}

	private void send(long subscriptionOrderId) {
		restTemplateSender.sendToPost(
			URL + subscriptionOrderId,
			null,
			null,
			String.class
		);
	}
}
