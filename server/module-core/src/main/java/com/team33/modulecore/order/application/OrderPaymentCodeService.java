package com.team33.modulecore.order.application;

import org.springframework.dao.DataAccessException;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.team33.modulecore.common.OrderFindHelper;
import com.team33.modulecore.exception.DataSaveException;
import com.team33.modulecore.order.domain.entity.Order;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Transactional
@Service
public class OrderPaymentCodeService {

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
	public void addSid(Order order, String sid) {
		try {
			order.addSid(sid);
		} catch (DataAccessException e) {
			log.error("orderId = {} :: lost sid = {}", order.getId(), sid);
			throw new DataSaveException(e.getMessage());
		}
	}
}
