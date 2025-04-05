package com.team33.modulecore.core.cart.application;

import static com.team33.modulecore.cache.CacheType.*;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Service;

import com.team33.modulecore.core.cart.domain.CartItemVO;
import com.team33.modulecore.core.cart.domain.CartVO;
import com.team33.modulecore.core.cart.domain.ItemVO;
import com.team33.modulecore.core.cart.domain.NormalCartVO;
import com.team33.modulecore.core.cart.domain.SubscriptionCartVO;
import com.team33.modulecore.core.cart.dto.SubscriptionContext;
import com.team33.modulecore.exception.BusinessLogicException;
import com.team33.modulecore.exception.ExceptionCode;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class MemoryCartClient {

	private final CacheManager cacheManager;

	public void saveCart(String key, CartVO cart) {

		Cache cache = cacheManager.getCache(CARTS.name());
		if (cache != null) {
			cache.put(key, cart);
		}
	}

	public void addNormalItem(String key, ItemVO item, int quantity) {

		NormalCartVO normalCartVO = getCart(key, NormalCartVO.class);
		checkDuplication(item, normalCartVO);

		CartItemVO cartItem = CartItemVO.of(item, quantity);
		normalCartVO.addNormalItem(cartItem);
		saveCart(key, normalCartVO);
	}

	public void addSubscriptionItem(String key, SubscriptionContext subscriptionContext) {

		SubscriptionCartVO subscriptionCart = getCart(key, SubscriptionCartVO.class);

		CartItemVO cartItem = CartItemVO.of(
			subscriptionContext.getItem(),
			subscriptionContext.getQuantity(),
			subscriptionContext.getSubscriptionInfo()
		);
		subscriptionCart.addSubscriptionItem(cartItem);

		saveCart(key, subscriptionCart);
	}

	public void deleteCartItem(String key, long itemId) {

		CartVO cart = getCart(key, CartVO.class);
		CartItemVO targetItem = getCartItem(itemId, cart);

		targetItem.remove(cart);
		cart.removeCartItem(targetItem);

		saveCart(key, cart);
	}

	public void changeQuantity(String key, long itemId, int quantity) {

		CartVO cart = getCart(key, CartVO.class);
		CartItemVO targetItem = getCartItem(itemId, cart);
		changeQuantity(quantity, targetItem, cart);

		saveCart(key, cart);
	}

	public void refresh(String key) {

		CartVO cart = getCart(key, CartVO.class);

		if (cart.getCartItems().isEmpty()) {
			return;
		}

		removeAllCartItem(cart);
		saveCart(key, cart);
	}

	public void refreshOrderedItem(String key, List<Long> orderedItemIds) {

		CartVO cart = getCart(key, CartVO.class);
		removeOrderedItem(cart, orderedItemIds);

		saveCart(key, cart);
	}

	public void changePeriod(String key, long itemId, int period) {

		SubscriptionCartVO cart = getCart(key, SubscriptionCartVO.class);
		CartItemVO targetItem = getCartItem(itemId, cart);

		targetItem.changePeriod(period);
		saveCart(key, cart);
	}

	public <T extends CartVO> T getCart(String key, Class<T> cartType) {

		Cache cache = cacheManager.getCache(CARTS.name());
		if (isNotNullCache(key, cache, cartType)) {
			return cache.get(key, cartType);
		}
		throw new BusinessLogicException(ExceptionCode.CART_NOT_FOUND);
	}

	private boolean isNotNullCache(String key, Cache cache, Class<?> cartType) {

		return cache != null && cache.get(key, cartType) != null;
	}

	private void checkDuplication(ItemVO item, NormalCartVO normalCart) {

		boolean isIn = normalCart.getCartItems()
			.stream()
			.anyMatch(cartItemVO -> cartItemVO.getItem().getId().equals(item.getId()));

		if (isIn) {
			throw new IllegalArgumentException("이미 장바구니에 있습니다.");
		}
	}

	private CartItemVO getCartItem(long itemId, CartVO cartVO) {

		return cartVO.getCartItems().stream()
			.filter(
				cartItem -> cartItem.getItem().getId().equals(itemId)
			)
			.findFirst()
			.orElseThrow(() -> new BusinessLogicException(ExceptionCode.CART_ITEM_NOT_FOUND));
	}

	private void changeQuantity(int quantity, CartItemVO cartItem, CartVO cart) {

		if (cartItem.getTotalQuantity() == quantity) {
			return;
		}
		cart.changeCartItemQuantity(cartItem, quantity);
	}

	private void removeAllCartItem(CartVO cartVO) {

		cartVO.getCartItems()
			.forEach(cartItem -> cartItem.remove(cartVO));

		cartVO.getCartItems().clear();
	}

	private void removeOrderedItem(CartVO cart, List<Long> orderedItemIds) {

		List<CartItemVO> removedItems = cart.getCartItems()
			.stream()
			.filter(cartItem -> orderedItemIds.contains(cartItem.getItem().getId()))
			.collect(Collectors.toUnmodifiableList());

		removedItems
			.forEach(cartItem -> cartItem.remove(cart));

		removedItems.forEach(cart::removeCartItem);
	}
}
