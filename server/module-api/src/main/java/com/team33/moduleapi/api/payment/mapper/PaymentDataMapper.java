package com.team33.moduleapi.api.payment.mapper;

import java.util.concurrent.TimeUnit;

import org.springframework.stereotype.Component;

import net.jodah.expiringmap.ExpiringMap;

@Component
public class PaymentDataMapper {

	private final ExpiringMap<Long, PaymentData> store = ExpiringMap.builder()
		.expiration(10, TimeUnit.MINUTES)
		.build();

	public void addData(Long targetId, String tid) {
		PaymentData data = PaymentData.builder()
			.targetId(targetId)
			.tid(tid)
			.build();

		store.put(targetId, data);
	}

	public PaymentData getData(Long targetId) {
		return store.get(targetId);
	}
}
