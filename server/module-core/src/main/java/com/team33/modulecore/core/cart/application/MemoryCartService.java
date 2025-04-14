package com.team33.modulecore.core.cart.application;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.team33.modulecore.core.cart.dto.SubscriptionContext;
import com.team33.modulecore.core.cart.vo.CartItemVO;
import com.team33.modulecore.core.cart.vo.CartVO;
import com.team33.modulecore.core.cart.vo.ItemVO;
import com.team33.modulecore.core.cart.vo.NormalCartVO;
import com.team33.modulecore.core.cart.vo.SubscriptionCartVO;
import com.team33.modulecore.exception.BusinessLogicException;
import com.team33.modulecore.exception.ExceptionCode;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Service
public class MemoryCartService {

	private final CartCacheManager cartCacheManager;
	private final CartValidator cartValidator;

	public void addNormalItem(String key, ItemVO item, int quantity) {

		NormalCartVO cart = cartCacheManager.getCart(key, NormalCartVO.class)
			.orElseThrow(() -> new BusinessLogicException(ExceptionCode.CART_NOT_FOUND));

		cartValidator.checkDuplication(item.getId(), cart);
		addItemToNormalCart(item, quantity, cart, key);
	}

	public void addSubscriptionItem(String key, SubscriptionContext context) {

		SubscriptionCartVO cart = cartCacheManager.getCart(key, SubscriptionCartVO.class)
			.orElseThrow(() -> new BusinessLogicException(ExceptionCode.CART_NOT_FOUND));

		cartValidator.checkDuplication(context.getItem().getId(), cart);
		addItemToSubscriptionCart(context, cart, key);
	}

	public <T extends CartVO> void deleteCartItem(String key, long itemId, Class<T> type) {

		T cart = getCart(key, type)
			.orElseThrow(() -> new BusinessLogicException(ExceptionCode.CART_NOT_FOUND));
		CartItemVO targetItem = getCartItem(itemId, cart);

		cart.removeCartItem(targetItem);
		initializationVOLog(cart);
		cartCacheManager.saveCart(key, cart);
	}

	public void changeQuantity(String key, long itemId, int quantity) {

		cartValidator.validateQuantity(quantity);
		CartVO cart = getCart(key);
		CartItemVO targetItem = getCartItem(itemId, cart);

		targetItem.changeQuantity(quantity);
		initializationVOLog(cart);
		cartCacheManager.saveCart(key, cart);
	}

	public void changePeriod(String key, Long itemId, int period) {

		cartValidator.validatePeriod(period);
		CartVO cart = getCart(key);
		CartItemVO cartItem = getCartItem(itemId, cart);

		cartItem.changePeriod(period);
		initializationVOLog(cart);
		cartCacheManager.saveCart(key, cart);
	}

	public void refreshOrderedItem(String key, List<Long> orderedItemsId) {

		CartVO cart = getCart(key);

		List<CartItemVO> itemsToRemove = cart.getCartItems().stream()
			.filter(cartItem -> orderedItemsId.contains(cartItem.getItem().getId()))
			.collect(Collectors.toUnmodifiableList());

		itemsToRemove.forEach(cart::removeCartItem);
		initializationVOLog(cart);
		saveCart(key, cart);
	}

	public <T extends CartVO> Optional<T> getCart(String key, Class<T> type) {

		return cartCacheManager.getCart(key, type);
	}

	public <T extends CartVO> void saveCart(String key, T vo) {

		cartCacheManager.saveCart(key, vo);
	}

	private CartVO getCart(String key) {

		return cartCacheManager.getCart(key, CartVO.class)
			.orElseThrow(() -> new BusinessLogicException(ExceptionCode.CART_NOT_FOUND));
	}

	private void addItemToNormalCart(ItemVO item, int quantity, CartVO cart, String key) {

		CartItemVO cartItem = CartItemVO.of(item, quantity);
		cart.addCartItems(cartItem);
		initializationVOLog(cart);
		cartCacheManager.saveCart(key, cart);
	}

	private void addItemToSubscriptionCart(SubscriptionContext context, CartVO cart, String key) {

		CartItemVO cartItem = CartItemVO.of(context.getItem(), context.getQuantity(),
			context.getSubscriptionInfo());
		cart.addCartItems(cartItem);
		initializationVOLog(cart);
		cartCacheManager.saveCart(key, cart);
	}

	private <T extends CartVO> CartItemVO getCartItem(long itemId, T cartVO) {

		if (cartVO == null) {
			throw new BusinessLogicException(ExceptionCode.CART_NOT_FOUND);
		}

		return cartVO.getCartItems().stream()
			.filter(cartItem -> cartItem.getItem() != null)
			.filter(cartItem -> cartItem.getItem().getId().equals(itemId))
			.findFirst()
			.orElseThrow(() -> new BusinessLogicException(ExceptionCode.CART_ITEM_NOT_FOUND));
	}

	private void initializationVOLog(CartVO cart) {

		log.debug("cart.getCartItemVOs size(강제 초기화)= {}", cart.getCartItems().size());
	}
}
