package com.team33.modulebatch.listener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.SkipListener;

import com.team33.modulebatch.OrderVO;

public class ItemSkipListener implements SkipListener<OrderVO, OrderVO> {

	private static final Logger LOGGER = LoggerFactory.getLogger("fileLog");

	@Override
	public void onSkipInRead(Throwable t) {
		LOGGER.warn("item reader skip ::: exception = {}", t.getMessage());
	}

	@Override
	public void onSkipInWrite(OrderVO orderVO, Throwable throwable) {
		LOGGER.warn("item writer skip 통신 실패 ::: orderId = {}, exception = {}", orderVO.getOrderId(), throwable.getMessage());
	}

	@Override
	public void onSkipInProcess(OrderVO orderVO, Throwable throwable) {

	}
}
