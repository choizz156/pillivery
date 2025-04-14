package com.team33.modulecore.core.cart.application;

import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.team33.modulecore.core.cart.domain.entity.NormalCartEntity;
import com.team33.modulecore.core.cart.domain.repository.CartRepository;
import com.team33.modulecore.core.cart.mapper.CartVOMapper;
import com.team33.modulecore.core.cart.vo.NormalCartVO;
import com.team33.modulecore.exception.BusinessLogicException;
import com.team33.modulecore.exception.ExceptionCode;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class NormalCartItemService {

    private final CartRepository cartRepository;
    private final MemoryCartService memoryCartService;

	@Transactional(readOnly = true)
	public NormalCartVO findCart(String key, long cartId) {
		return getNormalCart(key, cartId);
	}

	private NormalCartVO getNormalCart(String key, long cartId) {
        return getCachedCart(key)
            .orElseGet(() -> getCartFromDatabase(key, cartId));
    }

    private Optional<NormalCartVO> getCachedCart(String key) {
        try {
            return memoryCartService.getCart(key, NormalCartVO.class);
        } catch (BusinessLogicException e) {
            return Optional.empty();
        }
    }

    private NormalCartVO getCartFromDatabase(String key, long cartId) {
        NormalCartEntity normalCartEntity = findCartEntity(cartId);
        NormalCartVO normalCartVO = CartVOMapper.toNormalCartVO(normalCartEntity);
        memoryCartService.saveCart(key, normalCartVO);
        return normalCartVO;
    }

    private NormalCartEntity findCartEntity(long cartId) {
        return cartRepository.findNormalCartById(cartId)
            .orElseThrow(() -> new BusinessLogicException(ExceptionCode.CART_NOT_FOUND));
    }
}
