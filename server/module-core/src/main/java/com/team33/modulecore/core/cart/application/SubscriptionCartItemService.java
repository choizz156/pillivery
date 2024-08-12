package com.team33.modulecore.core.cart.application;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.team33.modulecore.core.cart.domain.CartItemVO;
import com.team33.modulecore.core.cart.domain.ItemVO;
import com.team33.modulecore.core.cart.domain.SubscriptionCartVO;
import com.team33.modulecore.core.cart.domain.entity.CartItemEntity;
import com.team33.modulecore.core.cart.domain.entity.SubscriptionCartEntity;
import com.team33.modulecore.core.cart.domain.repository.CartRepository;
import com.team33.modulecore.exception.BusinessLogicException;
import com.team33.modulecore.exception.ExceptionCode;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class SubscriptionCartItemService {

	private final CartRepository cartRepository;
	private final MemoryCartClient memoryCartClient;

	@Transactional(readOnly = true)
	public SubscriptionCartVO findCart(String key, long cartId) {
		return getSubscriptionCart(cartId, key);
	}

	private SubscriptionCartVO getSubscriptionCart(long cartId, String key) {
		System.out.println("=========="+ cartId);
		SubscriptionCartVO cachedSubscriptionCart = (SubscriptionCartVO)memoryCartClient.getCart(key);

		if (cachedSubscriptionCart == null) {

			SubscriptionCartEntity subscriptionCart = cartRepository.findSubscriptionCartById(cartId)
				.orElseThrow(() -> new BusinessLogicException(ExceptionCode.CART_NOT_FOUND));

			List<CartItemVO> cartItemVOList = getCartItemVOs(subscriptionCart);

			SubscriptionCartVO subscriptionCartVO = new SubscriptionCartVO(
				subscriptionCart.getId(),
				subscriptionCart.getPrice(),
				cartItemVOList
			);

			memoryCartClient.saveCart(key, subscriptionCartVO);

			return subscriptionCartVO;
		}

		return cachedSubscriptionCart;
	}

	private List<CartItemVO> getCartItemVOs(SubscriptionCartEntity subscriptionCart) {
		return subscriptionCart.getCartItemEntities().stream()
			.map(e -> CartItemVO.builder()
				.totalQuantity(e.getTotalQuantity())
				.subscriptionInfo(e.getSubscriptionInfo())
				.item(createItemVO(e))
				.id(e.getId())
				.build()
			).collect(Collectors.toList());
	}

	private ItemVO createItemVO(CartItemEntity e) {
		return ItemVO.builder()
			.enterprise(e.getItem().getInformation().getEnterprise())
			.productName(e.getItem().getProductName())
			.discountRate((int)e.getItem().getDiscountRate())
			.discountPrice(e.getItem().getDiscountPrice())
			.originPrice(e.getItem().getOriginPrice())
			.thumbnailUrl(e.getItem().getThumbnailUrl())
			.realPrice(e.getItem().getRealPrice())
			.build();
	}
}
