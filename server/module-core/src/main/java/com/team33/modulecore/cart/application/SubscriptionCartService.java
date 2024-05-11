package com.team33.modulecore.cart.application;

import java.util.Objects;

import javax.transaction.Transactional;

import org.springframework.stereotype.Service;

import com.team33.modulecore.cart.domain.SubscriptionCartItem;
import com.team33.modulecore.cart.domain.entity.SubscriptionCart;
import com.team33.modulecore.cart.repository.SubscriptionCartRepository;
import com.team33.modulecore.exception.BusinessLogicException;
import com.team33.modulecore.exception.ExceptionCode;
import com.team33.modulecore.item.domain.entity.Item;
import com.team33.modulecore.order.domain.SubscriptionInfo;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Transactional
@Service
public class SubscriptionCartService {

	private final SubscriptionCartRepository subscriptionCartRepository;

	// public void refreshCart(List<NormalCartItem> normalCartItems, boolean subscription) { // 가격과 아이템 종류 갱신
	// 	normalCartItems.forEach(ic -> {
	// 		NormalCart normalCart = findCart(ic.getNormalCart().getId());
	// 		calculatePriceAndItemSize(subscription, normalCartItems, normalCart);
	// 		normalCartRepository.save(normalCart);
	// 	});
	// }

	public SubscriptionCart findSubscriptionCart(Long cartId) {
		return subscriptionCartRepository.findById(cartId)
			.orElseThrow(() -> new BusinessLogicException(ExceptionCode.CART_NOT_FOUND));
	}

	public void addItem(
		Long cartId,
		Item item,
		SubscriptionInfo subscriptionInfo,
		int quantity
	) {
		SubscriptionCart subscriptionCart = findSubscriptionCart(cartId);

		subscriptionCart.addItem(item, quantity, subscriptionInfo);
	}

	public void changeQuantity(Long cartId, Item item, int quantity) {
		SubscriptionCart subscriptionCart = findSubscriptionCart(cartId);
		SubscriptionCartItem cartItem = getCartItem(item, subscriptionCart);
		cartItem.changeQuantity(quantity);
	}

	public SubscriptionCart correctSubscriptionCart(Long cartId, Item item) {
		SubscriptionCart subscriptionCart =findSubscriptionCart(cartId);

		subscriptionCart.removeCartItem(getCartItem(item, subscriptionCart));
		return subscriptionCart;
	}

	private SubscriptionCartItem getCartItem(Item item, SubscriptionCart subscriptionCart) {

		return subscriptionCart.getSubscriptionCartItems().stream().filter(
				subscriptionCartItem -> Objects.equals(subscriptionCartItem.getItem().getId(), item.getId())
			)
			.findFirst()
			.orElseThrow(() -> new BusinessLogicException(ExceptionCode.CART_ITEM_NOT_FOUND));
	}

	public void changePeriod(Long cartId, Item item, int period) {
		SubscriptionCart subscriptionCart = findSubscriptionCart(cartId);

		SubscriptionCartItem cartItem = getCartItem(item, subscriptionCart);
		cartItem.changePeriod(period);
	}
}
