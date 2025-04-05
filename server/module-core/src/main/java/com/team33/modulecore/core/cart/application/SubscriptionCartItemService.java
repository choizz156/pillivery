package com.team33.modulecore.core.cart.application;

import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.team33.modulecore.core.cart.domain.entity.SubscriptionCartEntity;
import com.team33.modulecore.core.cart.domain.repository.CartRepository;
import com.team33.modulecore.core.cart.dto.SubscriptionCartVO;
import com.team33.modulecore.core.cart.mapper.CartVOMapper;
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
        return getSubscriptionCart(key, cartId);
    }

    private SubscriptionCartVO getSubscriptionCart(String key, long cartId) {
        return getCachedCart(key)
            .orElseGet(() -> getCartFromDatabase(key, cartId));
    }

    private Optional<SubscriptionCartVO> getCachedCart(String key) {
        try {
            return Optional.ofNullable(memoryCartClient.getCart(key, SubscriptionCartVO.class));
        } catch (BusinessLogicException e) {
            return Optional.empty();
        }
    }

    private SubscriptionCartVO getCartFromDatabase(String key, long cartId) {

		SubscriptionCartEntity subscriptionCartEntity = findCartEntity(cartId);
        SubscriptionCartVO subscriptionCartVO = CartVOMapper.toSubscriptionCartVO(subscriptionCartEntity);
        memoryCartClient.saveCart(key, subscriptionCartVO);

        return subscriptionCartVO;
    }

    private SubscriptionCartEntity findCartEntity(long cartId) {
        return cartRepository.findSubscriptionCartById(cartId)
            .orElseThrow(() -> new BusinessLogicException(ExceptionCode.CART_NOT_FOUND));
    }
}
