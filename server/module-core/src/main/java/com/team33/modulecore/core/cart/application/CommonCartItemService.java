package com.team33.modulecore.core.cart.application;

import java.util.List;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class CommonCartItemService {

	private final MemoryCartClient memoryCartClient;

	public void removeCartItem(long cartId, long itemId) {
		memoryCartClient.deleteCartItem(CartKeySupplier.from(cartId), itemId);
	}

	public void changeQuantity(long cartId, long itemId, int quantity) {
		memoryCartClient.changeQuantity(CartKeySupplier.from(cartId), itemId, quantity);
	}

	public void refresh(long cartId, List<Long> orderedItemsId) {
		memoryCartClient.refreshOrderedItem(CartKeySupplier.from(cartId), orderedItemsId);
	}
}
