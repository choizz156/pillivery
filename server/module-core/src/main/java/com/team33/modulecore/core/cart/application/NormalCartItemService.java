package com.team33.modulecore.core.cart.application;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.team33.modulecore.core.cart.domain.CartItemVO;
import com.team33.modulecore.core.cart.domain.ItemVO;
import com.team33.modulecore.core.cart.domain.NormalCartVO;
import com.team33.modulecore.core.cart.domain.entity.CartItemEntity;
import com.team33.modulecore.core.cart.domain.entity.NormalCartEntity;
import com.team33.modulecore.core.cart.domain.repository.CartRepository;
import com.team33.modulecore.exception.BusinessLogicException;
import com.team33.modulecore.exception.ExceptionCode;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class NormalCartItemService {

	private final CartRepository cartRepository;
	private final MemoryCartClient memoryCartClient;

	@Transactional(readOnly = true)
	public NormalCartVO findCart(String key, long cartId) {
		return getCart(key, cartId);
	}

	private NormalCartVO getCart(String key, long cartId) {

		NormalCartVO cachedNormalCart = (NormalCartVO)memoryCartClient.getCart(key, NormalCartVO.class);

		if (cachedNormalCart == null) {
			NormalCartEntity normalCartEntity = cartRepository.findNormalCartById(cartId)
				.orElseThrow(() -> new BusinessLogicException(ExceptionCode.CART_NOT_FOUND));

			List<CartItemVO> cartItemVOList = getCartItemVOs(normalCartEntity);

			NormalCartVO normalCartVO = new NormalCartVO(
				normalCartEntity.getId(),
				normalCartEntity.getPrice(),
				cartItemVOList
			);

			memoryCartClient.saveCart(key, normalCartVO);

			return normalCartVO;
		}

		return cachedNormalCart;
	}

	private List<CartItemVO> getCartItemVOs(NormalCartEntity normalCartEntity) {
		return normalCartEntity.getCartItemEntities().stream()
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
