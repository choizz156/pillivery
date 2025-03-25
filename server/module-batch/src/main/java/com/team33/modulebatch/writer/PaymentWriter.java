package com.team33.modulebatch.writer;

import java.util.List;

import org.springframework.batch.item.ItemWriter;

import com.team33.modulebatch.OrderVO;
import com.team33.modulebatch.infra.PaymentApiDispatcher;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class PaymentWriter implements ItemWriter<OrderVO> {

	private final PaymentApiDispatcher paymentApiDispatcher;

	@Override
	public void write(List<? extends OrderVO> list) throws Exception {

		if(list.isEmpty()) {
			return;
		}

		paymentApiDispatcher.dispatch(list);
	}
}
