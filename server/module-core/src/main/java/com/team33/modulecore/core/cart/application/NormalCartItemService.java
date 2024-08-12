package com.team33.modulecore.core.cart.application;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.team33.modulecore.core.cart.domain.entity.NormalCart;
import com.team33.modulecore.core.cart.domain.repository.CartRepository;
import com.team33.modulecore.core.cart.domain.vo.CartItemVO;
import com.team33.modulecore.core.cart.domain.vo.NormalCartVO;
import com.team33.modulecore.exception.BusinessLogicException;
import com.team33.modulecore.exception.ExceptionCode;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class NormalCartItemService {
	private final CartRepository cartRepository;
	private final MemoryCartClient memoryCartClient;

	@Transactional
	public NormalCartVO findCart(String key, long cartId) {
		return getCart(key, cartId);
	}

	private NormalCartVO getCart(String key, long cartId) {
		NormalCartVO normalCart = (NormalCartVO)memoryCartClient.getCart(key);

		if (normalCart == null) {
			NormalCart normalCartEntity = cartRepository.findNormalCartById(cartId)
				.orElseThrow(() -> new BusinessLogicException(ExceptionCode.CART_NOT_FOUND));

			List<CartItemVO> cartItemVOList = normalCartEntity.getCartItems().stream()
				.map(e -> CartItemVO.builder()
					.totalQuantity(e.getTotalQuantity())
					.subscriptionInfo(e.getSubscriptionInfo())
					.item(e.getItem())
					.id(e.getId())
					.build()
				).collect(Collectors.toList());

			NormalCartVO normalCartVO = new NormalCartVO(normalCartEntity.getId(), normalCartEntity.getPrice(), cartItemVOList);

			memoryCartClient.saveCart(key, normalCartVO);

			return normalCartVO;
		}

		return normalCart;
	}
}
