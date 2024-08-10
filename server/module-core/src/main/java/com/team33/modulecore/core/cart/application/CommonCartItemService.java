package com.team33.modulecore.core.cart.application;

import java.util.List;
import java.util.stream.Collectors;

import javax.transaction.Transactional;

import org.springframework.stereotype.Service;

import com.team33.modulecore.core.cart.domain.entity.Cart;
import com.team33.modulecore.core.cart.domain.entity.CartItem;
import com.team33.modulecore.core.cart.domain.repository.CartRepository;
import com.team33.modulecore.exception.BusinessLogicException;
import com.team33.modulecore.exception.ExceptionCode;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Transactional
@Service
public class CommonCartItemService {

	private final CartRepository cartRepository;

	public Cart findCart(long cartId) {
		return cartRepository.findById(cartId)
			.orElseThrow(() -> new BusinessLogicException(ExceptionCode.CART_NOT_FOUND));
	}

	public void removeCartItem(long cartId, long cartItemId) {
		Cart cart = findCart(cartId);

		CartItem cartItem = getCartItem(cartItemId, cart);
		cartItem.remove(cart);
		cart.removeCartItem(cartItem);
	}

	public void changeQuantity(long cartId, long cartItemId, int quantity) {
		Cart cart = findCart(cartId);

		CartItem cartItem = getCartItem(cartItemId, cart);

		changeQuantity(quantity, cartItem, cart);
	}

	public void refresh(long cartId, List<Long> orderedItemsId) {
		Cart cart = findCart(cartId);

		if (cart.getCartItems().isEmpty()) {
			return;
		}

		removeOrderedItem(cart, orderedItemsId);
	}

	private CartItem getCartItem(long cartItemId, Cart cart) {

		return cart.getCartItems().stream()
			.filter(
				cartItem -> cartItem.getItem().getId().equals(cartItemId)
			)
			.findFirst()
			.orElseThrow(() -> new BusinessLogicException(ExceptionCode.CART_ITEM_NOT_FOUND));
	}

	private void changeQuantity(int quantity, CartItem cartItem, Cart cart) {
		if (cartItem.getTotalQuantity() == quantity) {
			return;
		}
		cart.changeCartItemQuantity(cartItem, quantity);
	}

	private void removeOrderedItem(Cart cart, List<Long> orderedItemId) {
		List<CartItem> removedItems = cart.getCartItems()
			.stream()
			.filter(cartItem -> orderedItemId.contains(cartItem.getItem().getId()))
			.collect(Collectors.toUnmodifiableList());

		removedItems.forEach(cartItem -> {
			cart.removeCartItem(cartItem);
			cartRepository.deleteById(cart.getId());
		});
	}
}
