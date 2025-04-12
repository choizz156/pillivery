package com.team33.modulecore.core.cart.application;

import static com.team33.modulecore.cache.CacheType.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.cache.Cache;
import org.springframework.stereotype.Service;

import com.team33.modulecore.cache.CacheClient;
import com.team33.modulecore.core.cart.dto.CartItemVO;
import com.team33.modulecore.core.cart.dto.CartVO;
import com.team33.modulecore.core.cart.dto.ItemVO;
import com.team33.modulecore.core.cart.dto.NormalCartVO;
import com.team33.modulecore.core.cart.dto.SubscriptionCartVO;
import com.team33.modulecore.core.cart.dto.SubscriptionContext;
import com.team33.modulecore.exception.BusinessLogicException;
import com.team33.modulecore.exception.ExceptionCode;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Service
public class MemoryCartClient {

	private final CacheClient cacheClient;

	public void saveCart(String key, CartVO cart) {

		Cache cache = cacheClient.getCache(CARTS.name());
		cache.put(key, cart);
	}

	public void addNormalItem(String key, ItemVO item, int quantity) {

		NormalCartVO normalCartVO = getCart(key, NormalCartVO.class);
		if (normalCartVO == null) {
			normalCartVO = new NormalCartVO();
		}
		checkDuplication(item, normalCartVO);

		CartItemVO cartItem = CartItemVO.of(item, quantity);
		normalCartVO.addNormalItem(cartItem);

		saveCart(key, normalCartVO);
	}

	public void addSubscriptionItem(String key, SubscriptionContext subscriptionContext) {

		SubscriptionCartVO subscriptionCart = getCart(key, SubscriptionCartVO.class);

		if (subscriptionCart == null) {
			subscriptionCart = new SubscriptionCartVO();
		}

		checkDuplication(subscriptionContext.getItem(), subscriptionCart);

		CartItemVO cartItem = CartItemVO.of(
			subscriptionContext.getItem(),
			subscriptionContext.getQuantity(),
			subscriptionContext.getSubscriptionInfo());

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

		validateQuantity(quantity);

		CartVO cart = getCart(key, CartVO.class);
		CartItemVO targetItem = getCartItem(itemId, cart);
		changeQuantity(quantity, targetItem, cart);

		saveCart(key, cart);
	}

	public void refreshOrderedItem(String key, List<Long> orderedItemIds) {

		CartVO cart = getCart(key, CartVO.class);
		removeOrderedItem(cart, orderedItemIds);

		saveCart(key, cart);
	}

	public void changePeriod(String key, long itemId, int period) {

		validatePeriod(period);

		SubscriptionCartVO cart = getCart(key, SubscriptionCartVO.class);
		CartItemVO targetItem = getCartItem(itemId, cart);

		targetItem.changePeriod(period);
		saveCart(key, cart);
	}

	public <T extends CartVO> T getCart(String key, Class<T> cartType) {

		Cache cache = cacheClient.getCache(CARTS.name());
		return cache.get(key, cartType);
	}

	private void validatePeriod(int period) {

		if (period < 1) {
			throw new IllegalArgumentException("구독 기간은 1일 이상이어야 합니다.");
		}
	}

	private void validateQuantity(int quantity) {

		if (quantity <= 0) {
			throw new IllegalArgumentException("수량은 1개 이상이어야 합니다.");
		}
	}

	private void checkDuplication(ItemVO item, CartVO cartVO) {

		 Long targetItemId = Optional.ofNullable(item.getId())
			.orElseThrow(() -> new BusinessLogicException(ExceptionCode.ITEM_NOT_FOUND));

		List<CartItemVO> cartItemsCopy;
		synchronized (cartVO.getCartItems()) {
			cartItemsCopy = new ArrayList<>(
				Optional.ofNullable(cartVO.getCartItems())
					.orElse(Collections.emptyList()));
		}

		boolean exists = cartItemsCopy.stream()
			.filter(Objects::nonNull)
			.map(CartItemVO::getItem)
			.filter(Objects::nonNull)
			.map(ItemVO::getId)
			.filter(Objects::nonNull)
			.anyMatch(targetItemId::equals);

		if (exists) {
			throw new IllegalArgumentException(
				String.format("Item %d is already in the cart", targetItemId));
		}

		// List<CartItemVO> cartItems = cartVO.getCartItems();
		// if (cartItems == null || cartItems.isEmpty()) {
		// return;
		// }
		//
		// boolean isIn = cartVO.getCartItems()
		// .stream()
		// .filter(Objects::nonNull) // null인 요소는 건너뛰도록 필터링
		// .filter(cartItemVO -> cartItemVO.getItem() != null) // cartItemVO.getItem()이
		// null이 아닌 경우만 처리
		// .anyMatch(cartItemVO -> {
		// Long currentItemId = cartItemVO.getItem().getId();
		// return currentItemId != null && currentItemId.equals(item.getId());
		// });
		//
		// if (isIn) {
		// throw new IllegalArgumentException("이미 장바구니에 있습니다.");
		// }
	}

	private CartItemVO getCartItem(long itemId, CartVO cartVO) {

		return cartVO.getCartItems().stream()
			.filter(
				cartItem -> cartItem.getItem().getId().equals(itemId))
			.findFirst()
			.orElseThrow(() -> new BusinessLogicException(ExceptionCode.CART_ITEM_NOT_FOUND));
	}

	private void changeQuantity(int quantity, CartItemVO cartItem, CartVO cart) {

		if (cartItem.getTotalQuantity() == quantity) {
			return;
		}
		cart.changeCartItemQuantity(cartItem, quantity);
	}

	private void removeOrderedItem(CartVO cart, List<Long> orderedItemIds) {

		List<CartItemVO> removedItems = cart.getCartItems()
			.stream()
			.filter(cartItem -> orderedItemIds.contains(cartItem.getItem().getId()))
			.collect(Collectors.toUnmodifiableList());

		removedItems.forEach(cartItem -> cartItem.remove(cart));
		removedItems.forEach(cart::removeCartItem);
	}

}

