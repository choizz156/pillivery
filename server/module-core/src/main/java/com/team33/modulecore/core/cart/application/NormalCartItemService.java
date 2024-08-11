package com.team33.modulecore.core.cart.application;

import javax.transaction.Transactional;

import org.springframework.stereotype.Service;

import com.team33.modulecore.core.cart.domain.entity.NormalCart;
import com.team33.modulecore.core.cart.domain.repository.CartRepository;
import com.team33.modulecore.core.item.domain.entity.Item;
import com.team33.modulecore.exception.BusinessLogicException;
import com.team33.modulecore.exception.ExceptionCode;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Transactional
@Service
public class NormalCartItemService {

	private final CartRepository cartRepository;
	private final MemoryCartService memoryCartService;

	public NormalCart findCart(long cartId) {
		String key = CartKeySupplier.from(cartId);
		NormalCart cachedNormalCart = (NormalCart)memoryCartService.getCart(key);

		if (cachedNormalCart == null) {
			NormalCart normalCart = cartRepository.findNormalCartById(cartId)
				.orElseThrow(() -> new BusinessLogicException(ExceptionCode.CART_NOT_FOUND));

			memoryCartService.saveCart(key, normalCart);

			return normalCart;
		}

		return cachedNormalCart;
	}

	public void addItem(long cartId, Item item, int quantity) {
		memoryCartService.addNormalItem(CartKeySupplier.from(cartId), item, quantity);
	}
}
