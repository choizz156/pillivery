package com.team33.modulecore.core.order.application;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.team33.modulecore.core.common.OrderFindHelper;
import com.team33.modulecore.core.order.domain.entity.Order;
import com.team33.modulecore.exception.DataSaveException;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Transactional
@Service
public class OrderPaymentCodeService {

	private final static Logger log = LoggerFactory.getLogger("fileLog");

	private final OrderFindHelper orderFindHelper;

	@Async
	public void addTid(Long orderId, String tid) {
		Order order = orderFindHelper.findOrder(orderId);

		try {
			order.addTid(tid);
		} catch (DataAccessException e) {
			log.error("orderId = {} :: lost tid = {}", orderId, tid);
			throw new DataSaveException(e.getMessage());
		}
	}

	@Async
	public void addSid(long orderId, String sid) {
		Order order = orderFindHelper.findOrder(orderId);
		try {
			order.addSid(sid);
		} catch (DataAccessException e) {
			log.error("orderId = {} :: lost sid = {}", order.getId(), sid);
			throw new DataSaveException(e.getMessage());
		}
	}
}
