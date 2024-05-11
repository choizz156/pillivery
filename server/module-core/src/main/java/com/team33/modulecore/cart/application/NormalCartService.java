package com.team33.modulecore.cart.application;

import java.util.Objects;

import javax.transaction.Transactional;

import org.springframework.stereotype.Service;

import com.team33.modulecore.cart.domain.NormalCartItem;
import com.team33.modulecore.cart.domain.entity.NormalCart;
import com.team33.modulecore.cart.repository.NormalCartRepository;
import com.team33.modulecore.exception.BusinessLogicException;
import com.team33.modulecore.exception.ExceptionCode;
import com.team33.modulecore.item.domain.entity.Item;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Transactional
@Service
public class NormalCartService {

	private final NormalCartRepository normalCartRepository;

	// public void refreshCart(List<NormalCartItem> normalCartItems, boolean subscription) { // 가격과 아이템 종류 갱신
	// 	normalCartItems.forEach(ic -> {
	// 		NormalCart normalCart = findCart(ic.getNormalCart().getId());
	// 		calculatePriceAndItemSize(subscription, normalCartItems, normalCart);
	// 		normalCartRepository.save(normalCart);
	// 	});
	// }

	public NormalCart findNormalCart(Long cartId) {
		return normalCartRepository.findById(cartId)
			.orElseThrow(() -> new BusinessLogicException(ExceptionCode.CART_NOT_FOUND));
	}

	public void addItem(Long cartId, Item item, int quantity) {
		NormalCart normalCart = findNormalCart(cartId);

		normalCart.addItem(item, quantity);
	}

	public NormalCart correctNormalCart(Long cartId, Item item) {

		NormalCart normalCart = findNormalCart(cartId);

		normalCart.removeCartItem(getCartItem(item, normalCart));
		return normalCart;
	}

	private NormalCartItem getCartItem(Item item, NormalCart normalCart) {

		return normalCart.getNormalCartItems().stream().filter(
				normalCartItem -> Objects.equals(normalCartItem.getItem().getId(), item.getId())
			)
			.findFirst()
			.orElseThrow(() -> new BusinessLogicException(ExceptionCode.CART_ITEM_NOT_FOUND));
	}

	public void changeQuantity(Long cartId, Item item, int quantity) {
		NormalCart normalCart = findNormalCart(cartId);
		NormalCartItem cartItem = getCartItem(item, normalCart);
		cartItem.changeQuantity(quantity);
	}
}
