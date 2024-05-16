package com.team33.modulecore.cart.application;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import javax.transaction.Transactional;

import org.springframework.stereotype.Service;

import com.team33.modulecore.cart.domain.SubscriptionCartItem;
import com.team33.modulecore.cart.domain.entity.Cart;
import com.team33.modulecore.cart.domain.repository.CartRepository;
import com.team33.modulecore.exception.BusinessLogicException;
import com.team33.modulecore.exception.ExceptionCode;
import com.team33.modulecore.item.domain.entity.Item;
import com.team33.modulecore.order.domain.OrderItem;
import com.team33.modulecore.order.domain.SubscriptionInfo;

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

	public void addItem(
		Long cartId,
		int quantity,
		Item item,
		SubscriptionInfo subscriptionInfo
	) {
		Cart cart = findCart(cartId);

		cart.addSubscriptionItem(item, quantity, subscriptionInfo);
	}

	public void removeCartItem(Long cartId, Item item) {
		Cart cart = findCart(cartId);

		cart.removeSubscriptionCartItem(getSubscriptionCartItem(item, cart));
	}

	public void changeQuantity(Long cartId, Item item, int quantity) {
		Cart cart = findCart(cartId);

		cart.changeSubscriptionCartItemQuantity(getSubscriptionCartItem(item, cart), quantity);
	}

	public void changePeriod(Long cartId, Item item, int period) {
		Cart cart = findCart(cartId);

		getSubscriptionCartItem(item, cart).changePeriod(period);
	}

	public void refresh(Long cartId, List<OrderItem> orderItems) {
		Cart cart = findCart(cartId);

		if (cart.getSubscriptionCartItems().isEmpty()) {
			return;
		}

		List<Long> orderedItemId = getOrderedItemId(orderItems);

		removeOrderedItem(cart, orderedItemId);
	}

	private void removeOrderedItem(Cart cart, List<Long> orderedItemId) {
		cart.getSubscriptionCartItems()
			.stream()
			.filter(subscriptionCartItem -> orderedItemId.contains(subscriptionCartItem.getItem().getId()))
			.forEach(cart::removeSubscriptionCartItem);
	}

	private List<Long> getOrderedItemId(List<OrderItem> orderItems) {
		return orderItems
			.stream()
			.map(orderItem -> orderItem.getItem().getId())
			.collect(Collectors.toList());
	}

	private SubscriptionCartItem getSubscriptionCartItem(Item item, Cart cart) {

		return cart.getSubscriptionCartItems().stream()
			.filter(
				subscriptionCartItem -> Objects.equals(subscriptionCartItem.getItem().getId(), item.getId())
			)
			.findFirst()
			.orElseThrow(() -> new BusinessLogicException(ExceptionCode.CART_ITEM_NOT_FOUND));
	}
}
