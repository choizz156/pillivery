package com.team33.modulebatch.step;

import java.time.Duration;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.stereotype.Component;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.team33.modulebatch.domain.ErrorStatus;
import com.team33.modulebatch.domain.entity.DelayedItem;
import com.team33.modulebatch.infra.RestTemplateSender;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Component
public class RetryItemProcessor implements ItemProcessor<DelayedItem, DelayedItem> {

	private static final Logger LOGGER = LoggerFactory.getLogger("fileLog");
	private static final String HOST = "www.pv-alb.r-e.kr:8080";
	private static final String URL = HOST + "/api/payments/approve/subscriptions/";

	private final Cache<String, String> idempotencyCache = Caffeine.newBuilder()
		.maximumSize(1000)
		.expireAfterWrite(Duration.ofMinutes(30L))
		.build();

	private final RestTemplateSender restTemplateSender;
	private Long jobId;

	@Override
	public DelayedItem process(DelayedItem delayedItem) throws Exception {

		if (jobId == null) {
			LOGGER.warn("jobId is null");
			throw new IllegalStateException("JobId가 설정돼있어야 합니다.");
		}

		String idempotencyKey =
			jobId + "_"
				+ delayedItem.getSubscriptionOrderId()
				+ "_"
				+ delayedItem.getOriginalPaymentDate()
				+ "_"
				+ delayedItem.getRetryCount() + "_"
				+ "RT";

		checkAlreadyTry(idempotencyKey);
		delayedItem.setIdempotencyKey(idempotencyKey);

		send(delayedItem.getSubscriptionOrderId());
		idempotencyCache.put(delayedItem.getIdempotencyKey(), "item write -> api process");

		delayedItem.updateStatus(ErrorStatus.SOLVED);

		return delayedItem;
	}

	public void initialize(Long jobId) {

		this.jobId = jobId;
	}

	private void checkAlreadyTry(String key) {

		if (idempotencyCache.getIfPresent(key) != null) {
			throw new IllegalArgumentException("이미 시도된 아이템입니다. " + key);
		}
	}

	private void send(long subscriptionOrderId) {

		restTemplateSender.sendToPost(
			subscriptionOrderId,
			URL + subscriptionOrderId,
			null
		);
	}
}
