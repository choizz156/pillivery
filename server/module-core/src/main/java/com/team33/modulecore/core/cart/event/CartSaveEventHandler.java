package com.team33.modulecore.core.cart.event;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;
import org.springframework.dao.DataAccessException;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.team33.modulecore.core.cart.domain.entity.CartEntity;
import com.team33.modulecore.core.cart.domain.repository.CartRepository;
import com.team33.modulecore.exception.DataSaveException;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class CartSaveEventHandler {

	private static final Logger log = LoggerFactory.getLogger("fileLog");

	private final CartRepository cartRepository;
	private final CartEntityMapper cartEntityMapper;

	@Async
	@EventListener
	@Transactional
	public void onCartSavedEvent(CartSavedEvent event) {
		try {
			CartEntity cartEntity = cartEntityMapper.to(event.getExpiredCartVO());
			cartRepository.save(cartEntity);
		} catch (DataAccessException e) {
			log.warn("장바구니 영속화 실패 cartId : {}, message : {} ", event.getId(), e.getMessage());
			throw new DataSaveException(e.getMessage());
		}
	}
}
