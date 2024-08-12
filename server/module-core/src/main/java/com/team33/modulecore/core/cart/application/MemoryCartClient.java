package com.team33.modulecore.core.cart.application;

import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import org.redisson.api.RMapCache;
import org.redisson.api.RedissonClient;
import org.redisson.api.map.event.EntryExpiredListener;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import com.team33.modulecore.core.cart.domain.CartItemVO;
import com.team33.modulecore.core.cart.domain.CartVO;
import com.team33.modulecore.core.cart.domain.ItemVO;
import com.team33.modulecore.core.cart.domain.NormalCartVO;
import com.team33.modulecore.core.cart.domain.SubscriptionCartVO;
import com.team33.modulecore.core.cart.dto.SubscriptionContext;
import com.team33.modulecore.core.cart.event.CartSavedEvent;
import com.team33.modulecore.exception.BusinessLogicException;
import com.team33.modulecore.exception.ExceptionCode;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class MemoryCartClient {

	private static final long CART_TIME = 2L;

	private final RedissonClient redissonClient;
	private final ApplicationEventPublisher applicationEventPublisher;

	public CartVO getCart(String key) {
		RMapCache<String, CartVO> mapCache = getMapCache();
		return mapCache.get(key);
	}

	public void saveCart(String key, CartVO cart) {
		RMapCache<String, CartVO> mapCache = getMapCache();
		mapCache.put(key, cart, CART_TIME, TimeUnit.DAYS);

		mapCache.addListener((EntryExpiredListener<String, CartVO>)event -> {
			String eventKey = event.getKey();
			CartVO expiredCartEntity = event.getValue();

			String id = String.valueOf(eventKey.charAt(eventKey.length() - 1));
			applicationEventPublisher.publishEvent(new CartSavedEvent(Long.valueOf(id), expiredCartEntity));
		});
	}

	public void addNormalItem(String key, ItemVO item, int quantity) {
		NormalCartVO normalCartVO = (NormalCartVO)getMapCache().get(key);
		checkDuplication(item, normalCartVO);

		CartItemVO cartItem = CartItemVO.of(item, quantity);
		normalCartVO.addNormalItem(cartItem);
		saveCart(key, normalCartVO);
	}

	public void addSubscriptionItem(String key, SubscriptionContext subscriptionContext) {
		SubscriptionCartVO subscriptionCart = (SubscriptionCartVO)getMapCache().get(key);

		CartItemVO cartItem = CartItemVO.of(
			subscriptionContext.getItem(),
			subscriptionContext.getQuantity(),
			subscriptionContext.getSubscriptionInfo()
		);
		subscriptionCart.addSubscriptionItem(cartItem);

		saveCart(key, subscriptionCart);
	}

	public void deleteCartItem(String key, long itemId) {
		CartVO cart = getMapCache().get(key);
		CartItemVO targetItem = getCartItem(itemId, cart);

		targetItem.remove(cart);
		cart.removeCartItem(targetItem);

		saveCart(key, cart);
	}

	public void changeQuantity(String key, long itemId, int quantity) {
		CartVO cart = getMapCache().get(key);
		CartItemVO targetItem = getCartItem(itemId, cart);
		changeQuantity(quantity, targetItem, cart);

		saveCart(key, cart);
	}

	public void refresh(String key) {
		CartVO cart = getMapCache().get(key);

		if (cart.getCartItems().isEmpty()) {
			return;
		}
		removeAllCartItem(cart);

		saveCart(key, cart);
	}

	public void refreshOrderedItem(String key, List<Long> orderedItemIds) {
		CartVO cart = getMapCache().get(key);
		removeOrderedItem(cart, orderedItemIds);

		saveCart(key, cart);
	}

	public void changePeriod(String key, long itemId, int period) {
		SubscriptionCartVO cart = (SubscriptionCartVO)getMapCache().get(key);

		CartItemVO targetItem = getCartItem(itemId, cart);

		targetItem.changePeriod(period);
		saveCart(key, cart);
	}

	private void checkDuplication(ItemVO item, NormalCartVO normalCart) {
		boolean isIn = normalCart.getCartItems()
			.stream()
			.anyMatch(cartItemVO -> cartItemVO.getItem().getId().equals(item.getId()));

		if (isIn){
			throw new IllegalArgumentException("이미 장바구니에 있습니다.");
		}
	}

	private RMapCache<String, CartVO> getMapCache() {
		return redissonClient.getMapCache("cart");
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
