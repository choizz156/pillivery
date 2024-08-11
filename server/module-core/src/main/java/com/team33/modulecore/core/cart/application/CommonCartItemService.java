package com.team33.modulecore.core.cart.application;

import java.util.List;

import javax.transaction.Transactional;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Transactional
@Service
public class CommonCartItemService {

	private final MemoryCartService memoryCartService;

	public void removeCartItem(long cartId, long itemId) {
		memoryCartService.deleteCartItem(CartKeySupplier.from(cartId), itemId);
	}

	public void changeQuantity(long cartId, long itemId, int quantity) {
		memoryCartService.changeQuantity(CartKeySupplier.from(cartId), itemId, quantity);
	}

	public void refresh(long cartId, List<Long> orderedItemsId) {
		memoryCartService.refreshOrderedItem(CartKeySupplier.from(cartId), orderedItemsId);
	}
}
