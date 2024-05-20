package com.team33.moduleapi.ui.payment.mapper;

import java.util.concurrent.TimeUnit;

import org.springframework.stereotype.Component;

import net.jodah.expiringmap.ExpiringMap;

import com.team33.moduleapi.ui.payment.dto.PaymentData;

@Component
public class PaymentDataService {

	// private ThreadLocal<PaymentData> store = new ThreadLocal<>();
	private final ExpiringMap<Long, PaymentData> store = ExpiringMap.builder()
		.expiration(10, TimeUnit.MINUTES)
		.build();

	public void addData(Long orderId, String tid) {
		PaymentData data = PaymentData.builder()
			.orderId(orderId)
			.tid(tid)
			.build();

		store.put(orderId, data);
	}

	public PaymentData getData(Long orderId) {
		return store.get(orderId);
	}
}
