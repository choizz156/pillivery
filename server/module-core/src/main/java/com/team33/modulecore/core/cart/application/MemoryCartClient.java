package com.team33.modulecore.core.cart.application;

import java.util.concurrent.TimeUnit;

import org.redisson.api.RMapCache;
import org.redisson.api.RedissonClient;
import org.redisson.api.map.event.EntryExpiredListener;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import com.team33.modulecore.core.cart.domain.entity.Cart;
import com.team33.modulecore.core.cart.domain.vo.CartItemVO;
import com.team33.modulecore.core.cart.domain.vo.CartVO;
import com.team33.modulecore.core.cart.domain.vo.NormalCartVO;
import com.team33.modulecore.core.cart.event.CartSavedEvent;
import com.team33.modulecore.core.item.domain.entity.Item;

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

		mapCache.addListener((EntryExpiredListener<String, Cart>)event -> {
			String eventKey = event.getKey();
			Cart expiredCart = event.getValue();

			String id = String.valueOf(eventKey.charAt(eventKey.length() - 1));
			applicationEventPublisher.publishEvent(new CartSavedEvent(Long.valueOf(id), expiredCart));
		});
	}

	public void addNormalItem(String key, Item item, int quantity) {
		NormalCartVO normalCart = (NormalCartVO)getMapCache().get(key);
		CartItemVO cartItem = CartItemVO.of(item, quantity);
		normalCart.addNormalItem(cartItem);
		saveCart(key, normalCart);
	}

	private RMapCache<String, CartVO> getMapCache() {
		return redissonClient.getMapCache("cart");
	}

	// public void addSubscriptionItem(String key, SubscriptionContext subscriptionContext) {
	// 	SubscriptionCart subscriptionCart = (SubscriptionCart)getMapCache().get(key);
	//
	// 	CartItem cartItem = CartItem.of(
	// 		subscriptionContext.getItem(),
	// 		subscriptionContext.getQuantity(),
	// 		subscriptionContext.getSubscriptionInfo()
	// 	);
	// 	subscriptionCart.addSubscriptionItem(cartItem);
	//
	// 	saveCart(key, subscriptionCart);
	// }
	//
	// public void deleteCartItem(String key, long itemId) {
	// 	Cart cart = getMapCache().get(key);
	// 	CartItem targetItem = getCartItem(itemId, cart);
	//
	// 	targetItem.remove(cart);
	// 	cart.removeCartItem(targetItem);
	//
	// 	saveCart(key, cart);
	// }
	//
	// public void changeQuantity(String key, long itemId, int quantity) {
	// 	Cart cart = getMapCache().get(key);
	// 	CartItem targetItem = getCartItem(itemId, cart);
	// 	changeQuantity(quantity, targetItem, cart);
	//
	// 	saveCart(key, cart);
	// }
	//
	// public void refresh(String key) {
	// 	Cart cart = getMapCache().get(key);
	//
	// 	if (cart.getCartItems().isEmpty()) {
	// 		return;
	// 	}
	// 	removeAllCartItem(cart);
	//
	// 	saveCart(key, cart);
	// }
	//
	// public void refreshOrderedItem(String key, List<Long> orderedItemIds) {
	// 	Cart cart = getMapCache().get(key);
	// 	removeOrderedItem(cart, orderedItemIds);
	//
	// 	saveCart(key, cart);
	// }
	//
	// public void changePeriod(String key, long itemId, int period) {
	// 	SubscriptionCart cart = (SubscriptionCart)getMapCache().get(key);
	//
	// 	CartItem targetItem = getCartItem(itemId, cart);
	//
	// 	targetItem.changePeriod(period);
	// 	saveCart(key, cart);
	// }
	//
	// private CartItem getCartItem(long itemId, Cart cart) {
	//
	// 	return cart.getCartItems().stream()
	// 		.filter(
	// 			cartItem -> cartItem.getItem().getId().equals(itemId)
	// 		)
	// 		.findFirst()
	// 		.orElseThrow(() -> new BusinessLogicException(ExceptionCode.CART_ITEM_NOT_FOUND));
	// }
	//
	// private void changeQuantity(int quantity, CartItem cartItem, Cart cart) {
	// 	if (cartItem.getTotalQuantity() == quantity) {
	// 		return;
	// 	}
	// 	cart.changeCartItemQuantity(cartItem, quantity);
	// }
	//
	// private void removeAllCartItem(Cart cart) {
	// 	cart.getCartItems()
	// 		.forEach(cartItem -> cartItem.remove(cart));
	//
	// 	cart.getCartItems().clear();
	// }
	//
	// private void removeOrderedItem(Cart cart, List<Long> orderedItemIds) {
	// 	List<CartItem> removedItems = cart.getCartItems()
	// 		.stream()
	// 		.filter(cartItem -> orderedItemIds.contains(cartItem.getItem().getId()))
	// 		.collect(Collectors.toUnmodifiableList());
	//
	// 	removedItems
	// 		.forEach(cartItem -> cartItem.remove(cart));
	//
	// 	removedItems.forEach(cart::removeCartItem);
	// }
}
