package com.team33.modulebatch.infra;

import java.util.List;

import org.springframework.stereotype.Service;

import com.team33.modulebatch.OrderVO;
import com.team33.moduleexternalapi.infra.RestTemplateSender;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class PaymentApiDispatcher {

	private static final String HOST = "http://localhost:8080";
	private static final String URL = HOST + "/api/payments/approve/subscriptions/";
	private final RestTemplateSender restTemplateSender;

	public void dispatch(List<? extends OrderVO> list) {
		list.forEach(order -> send(order.getOrderId()));
	}

	private void send(long orderId) {
		restTemplateSender.sendToPost(
			URL + orderId,
			null,
			null,
			String.class
		);
	}
}
