package com.team33.modulecore.cart.application;

import java.util.ArrayList;
import java.util.List;

import javax.transaction.Transactional;

import org.springframework.stereotype.Service;

import com.team33.modulecore.cart.domain.NormalCartItem;
import com.team33.modulecore.cart.domain.entity.Cart;
import com.team33.modulecore.cart.domain.repository.CartRepository;
import com.team33.modulecore.exception.BusinessLogicException;
import com.team33.modulecore.exception.ExceptionCode;
import com.team33.modulecore.item.domain.entity.Item;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Transactional
@Service
public class NormalCartService {

	private final CartRepository cartRepository;

	public Cart findCart(Long cartId) {
		return cartRepository.findById(cartId)
			.orElseThrow(() -> new BusinessLogicException(ExceptionCode.CART_NOT_FOUND));
	}

	public void addItem(Long cartId, Item item, int quantity) {
		Cart cart = findCart(cartId);

		cart.addNormalItem(item, quantity);
	}

	public void removeCartItem(Long cartId, Item item) {
		Cart cart = findCart(cartId);

		cart.removeNormalCartItem(getNormalCartItem(item, cart));
	}

	public void changeQuantity(Long cartId, Item item, int quantity) {
		Cart cart = findCart(cartId);

		NormalCartItem normalCartItem = getNormalCartItem(item, cart);

		changeQuantity(quantity, normalCartItem, cart);
	}

	public List<NormalCartItem> findCartItem(Long cartId) {
		Cart cart = findCart(cartId);

		return new ArrayList<>(cart.getNormalCartItems());
	}

	private NormalCartItem getNormalCartItem(Item item, Cart cart) {

		return cart.getNormalCartItems().stream()
			.filter(
				normalCartItem -> normalCartItem.getItem().getId().equals(item.getId())
			)
			.findFirst()
			.orElseThrow(() -> new BusinessLogicException(ExceptionCode.CART_ITEM_NOT_FOUND));
	}

	private void changeQuantity(int quantity, NormalCartItem normalCartItem, Cart cart) {
		if (normalCartItem.getTotalQuantity() == quantity) {
			return;
		}

		cart.changeNormalCartItemQuantity(normalCartItem, quantity);
	}
}
