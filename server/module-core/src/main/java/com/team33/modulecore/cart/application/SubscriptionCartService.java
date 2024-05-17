package com.team33.modulecore.cart.application;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import javax.transaction.Transactional;

import org.springframework.stereotype.Service;

import com.team33.modulecore.cart.SubscriptionContext;
import com.team33.modulecore.cart.domain.SubscriptionCartItem;
import com.team33.modulecore.cart.domain.entity.Cart;
import com.team33.modulecore.cart.domain.repository.CartRepository;
import com.team33.modulecore.exception.BusinessLogicException;
import com.team33.modulecore.exception.ExceptionCode;
import com.team33.modulecore.item.domain.entity.Item;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Transactional
@Service
public class SubscriptionCartService {

	private final CartRepository cartRepository;

	public Cart findCart(Long cartId) {
		return cartRepository.findById(cartId)
			.orElseThrow(() -> new BusinessLogicException(ExceptionCode.CART_NOT_FOUND));
	}

	public void addItem(Long cartId, SubscriptionContext subscriptionContext) {
		Cart cart = findCart(cartId);

		cart.addSubscriptionItem(
			subscriptionContext.getItem(),
			subscriptionContext.getQuantity(),
			subscriptionContext.getSubscriptionInfo()
		);
	}

	public void removeCartItem(Long cartId, Item item) {
		Cart cart = findCart(cartId);

		cart.removeSubscriptionCartItem(getSubscriptionCartItem(item, cart));
	}

	public void changeQuantity(Long cartId, Item item, int quantity) {
		Cart cart = findCart(cartId);

		SubscriptionCartItem subscriptionCartItem = getSubscriptionCartItem(item, cart);
		changeQuantity(quantity, subscriptionCartItem, cart);
	}

	public void changePeriod(Long cartId, Item item, int period) {
		Cart cart = findCart(cartId);

		SubscriptionCartItem subscriptionCartItem = getSubscriptionCartItem(item, cart);

		subscriptionCartItem.changePeriod(period);
	}

	public void refresh(Long cartId, List<Long> orderItemsId) {
		Cart cart = findCart(cartId);

		if (cart.getSubscriptionCartItems().isEmpty()) {
			return;
		}

		removeOrderedItem(cart, orderItemsId);
	}

	private void removeOrderedItem(Cart cart, List<Long> orderedItemId) {
		List<SubscriptionCartItem> removeItems = cart.getSubscriptionCartItems()
			.stream()
			.filter(subscriptionCartItem -> orderedItemId.contains(subscriptionCartItem.getItem().getId()))
			.collect(Collectors.toUnmodifiableList());

		removeItems.forEach(cart::removeSubscriptionCartItem);
	}

	private SubscriptionCartItem getSubscriptionCartItem(Item item, Cart cart) {

		return cart.getSubscriptionCartItems().stream()
			.filter(
				subscriptionCartItem -> Objects.equals(subscriptionCartItem.getItem().getId(), item.getId())
			)
			.findFirst()
			.orElseThrow(() -> new BusinessLogicException(ExceptionCode.CART_ITEM_NOT_FOUND));
	}

	private void changeQuantity(int quantity, SubscriptionCartItem subscriptionCartItem, Cart cart) {
		if (subscriptionCartItem.getTotalQuantity() == quantity) {
			return;
		}
		cart.changeSubscriptionCartItemQuantity(subscriptionCartItem, quantity);
	}
}
