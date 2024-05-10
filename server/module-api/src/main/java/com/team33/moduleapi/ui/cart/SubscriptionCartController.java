package com.team33.moduleapi.ui.cart;

import javax.validation.constraints.Positive;

import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.team33.moduleapi.dto.SingleResponseDto;
import com.team33.moduleapi.ui.cart.dto.CartResponseDto;
import com.team33.moduleapi.ui.cart.dto.SubscriptionCartItemPostDto;
import com.team33.moduleapi.ui.cart.mapper.CartResponseMapper;
import com.team33.moduleapi.ui.cart.mapper.CartServiceMapper;
import com.team33.modulecore.cart.application.SubscriptionCartService;
import com.team33.modulecore.cart.domain.entity.SubscriptionCart;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Validated
@RequiredArgsConstructor
@RestController
public class SubscriptionCartController {

	private final SubscriptionCartService subscriptionCartService;

	private final CartServiceMapper cartServiceMapper;
	private final CartResponseMapper cartResponseMapper;

	@ResponseStatus(HttpStatus.CREATED)
	@PostMapping("/carts/{subscriptionCartId}")
	public SingleResponseDto<Long> postSubscriptionItemCart(
		@PathVariable Long subscriptionCartId,
		@Positive @RequestParam int quantity,
		@RequestParam Long itemId,
		SubscriptionCartItemPostDto postDto
	) {

		subscriptionCartService.addItem(
			subscriptionCartId,
			cartServiceMapper.toItem(itemId),
			cartServiceMapper.toSubscriptionInfo(postDto),
			quantity
		);

		return new SingleResponseDto<>(itemId);
	}

	@GetMapping("/carts/subscription/{cartId}")
	public SingleResponseDto<CartResponseDto> getSubscriptionCart(
		@PathVariable Long cartId
	) {

		SubscriptionCart subscriptionCart = subscriptionCartService.findSubscriptionCart(cartId);
		CartResponseDto cartResponseDto = cartResponseMapper.cartResponseDto(subscriptionCart);

		return new SingleResponseDto<>(cartResponseDto);
	}

	@DeleteMapping("/carts/subscription/{cartId}")
	public SingleResponseDto<CartResponseDto> patchSubscriptionCart(
		@PathVariable Long cartId,
		@RequestParam Long itemId
	) {

		SubscriptionCart subscriptionCart = subscriptionCartService.correctSubscriptionCart(
			cartId,
			cartServiceMapper.toItem(itemId)
		);
		CartResponseDto cartResponseDto = cartResponseMapper.cartResponseDto(subscriptionCart);

		return new SingleResponseDto<>(cartResponseDto);
	}
}

