package com.team33.modulecore.cart.application;

import java.util.Objects;

import javax.transaction.Transactional;

import org.springframework.stereotype.Service;

import com.team33.modulecore.cart.domain.NormalCart;
import com.team33.modulecore.cart.domain.SubscriptionCart;
import com.team33.modulecore.cart.repository.NormalCartRepository;
import com.team33.modulecore.cart.repository.SubscriptionCartRepository;
import com.team33.modulecore.common.UserFindHelper;
import com.team33.modulecore.exception.BusinessLogicException;
import com.team33.modulecore.exception.ExceptionCode;
import com.team33.modulecore.item.domain.entity.Item;
import com.team33.modulecore.item.domain.repository.ItemQueryRepository;
import com.team33.modulecore.itemcart.domain.NormalCartItem;
import com.team33.modulecore.itemcart.domain.SubscriptionCartItem;
import com.team33.modulecore.order.domain.SubscriptionInfo;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Transactional
@Service
public class CartService {

	private final NormalCartRepository normalCartRepository;
	private final SubscriptionCartRepository subscriptionCartRepository;
	// private final ItemCartService itemCartService;
	private final UserFindHelper userFindHelper;
	private final ItemQueryRepository itemQueryRepository;

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

	public NormalCart findNormalCart(Long cartId) {
		return normalCartRepository.findById(cartId)
			.orElseThrow(() -> new BusinessLogicException(ExceptionCode.CART_NOT_FOUND));
	}

	// public int countTotalDiscountPrice(long cartId, boolean subscription) {
	// 	NormalCart normalCart = findCart(cartId);
	// 	List<NormalCartItem> normalCartItems = itemCartService.findItemCarts(normalCart, subscription, true);
	//
	// 	if (normalCartItems == null) {
	// 		return 0;
	// 	}
	//
	// 	int totalDiscountPrice = 0;
	//
	// 	for (NormalCartItem normalCartItem : normalCartItems) {
	// 		int quantity = normalCartItem.getItem().getOriginPrice();
	// 		int price = normalCartItem.getQuantity();
	// 		double discountRate = normalCartItem.getItem().getDiscountRate();
	//
	// 		totalDiscountPrice += (int)(quantity * price * discountRate / 100);
	// 	}
	//
	// 	return totalDiscountPrice;
	// }
	//
	// private void calculatePriceAndItemSize(
	// 	boolean subscription,
	// 	List<NormalCartItem> normalCartItems,
	// 	NormalCart normalCart
	// ) {
	// 	int totalPrice = countTotalPrice(normalCartItems);
	// 	int totalItems = normalCartItems.size();
	//
	// 	if (subscription) {
	// 		normalCart.changeSubTotalPrice(totalPrice);
	// 		normalCart.changeSubTotalItems(totalItems);
	// 		return;
	// 	}
	//
	// 	normalCart.changeTotalPrice(totalPrice);
	// 	normalCart.changeTotalItems(totalItems);
	// }
	//
	// private int countTotalPrice(List<NormalCartItem> normalCartItems) {
	//
	// 	if (normalCartItems.isEmpty()) {
	// 		return 0;
	// 	}
	// 	int totalPrice = 0;
	//
	// 	for (NormalCartItem normalCartItem : normalCartItems) {
	// 		int quantity = normalCartItem.getItem().getOriginPrice();
	// 		int price = normalCartItem.getQuantity();
	// 		totalPrice += (quantity * price);
	// 	}
	//
	// 	return totalPrice;
	// }
	//
	public void addItem(Long normalCartId, Item item, int quantity) {
		NormalCart normalCart = normalCartRepository.findById(normalCartId)
			.orElseThrow(() -> new BusinessLogicException(ExceptionCode.CART_NOT_FOUND));

		normalCart.addItem(item, quantity);

	}

	public void addSubscriptionItem(
		Long subscriptionCartId,
		Item item,
		SubscriptionInfo subscriptionInfo,
		int quantity
	) {
		SubscriptionCart subscriptionCart = subscriptionCartRepository.findById(subscriptionCartId)
			.orElseThrow(() -> new BusinessLogicException(ExceptionCode.CART_NOT_FOUND));

		subscriptionCart.addItem(item, quantity, subscriptionInfo);
	}

	public NormalCart correctNormalCart(Long cartId, Item item) {

		NormalCart normalCart = normalCartRepository.findById(cartId)
			.orElseThrow(() -> new BusinessLogicException(ExceptionCode.CART_NOT_FOUND));

		normalCart.getNormalCartItems().remove(getRemovedItem(item, normalCart));
		return normalCart;
	}

	public SubscriptionCart correctSubscriptionCart(Long cartId, Item item) {
		SubscriptionCart subscriptionCart = subscriptionCartRepository.findById(cartId)
			.orElseThrow(() -> new BusinessLogicException(ExceptionCode.CART_NOT_FOUND));

		subscriptionCart.getSubscriptionCartItems().remove(getRemovedItem(item, subscriptionCart));
		return subscriptionCart;
	}

	private NormalCartItem getRemovedItem(Item item, NormalCart normalCart) {

		return normalCart.getNormalCartItems().stream().filter(
				normalCartItem -> !Objects.equals(normalCartItem.getItem().getId(), item.getId())
			)
			.findFirst()
			.orElseThrow(() -> new BusinessLogicException(ExceptionCode.CART_ITEM_NOT_FOUND));
	}

	private SubscriptionCartItem getRemovedItem(Item item, SubscriptionCart subscriptionCart) {

		return subscriptionCart.getSubscriptionCartItems().stream().filter(
				subscriptionCartItem -> !Objects.equals(subscriptionCartItem.getItem().getId(), item.getId())
			)
			.findFirst()
			.orElseThrow(() -> new BusinessLogicException(ExceptionCode.CART_ITEM_NOT_FOUND));
	}
}
