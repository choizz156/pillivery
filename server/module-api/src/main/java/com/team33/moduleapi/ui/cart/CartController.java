package com.team33.moduleapi.ui.cart;

import javax.validation.constraints.Positive;

import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
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
import com.team33.modulecore.cart.application.CartService;
import com.team33.modulecore.cart.domain.NormalCart;
import com.team33.modulecore.cart.domain.SubscriptionCart;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Validated
@RequiredArgsConstructor
@RestController
public class CartController {

	private final CartService cartService;
	private final CartServiceMapper cartServiceMapper;
	private final CartResponseMapper cartResponseMapper;
	// private final CartMapper cartMapper;
	// private final ItemCartService itemCartService;
	// private final ItemMapper itemMapper;
	// private final ItemCartMapper itemCartMapper;

	@ResponseStatus(HttpStatus.CREATED)
	@PostMapping("/carts/{normalCartId}")
	public SingleResponseDto<Long> postNormalItemCart(
		@PathVariable Long normalCartId,
		@Positive @RequestParam int quantity,
		@RequestParam Long itemId
	) {
		cartService.addItem(normalCartId, cartServiceMapper.toItem(itemId), quantity);

		return new SingleResponseDto<>(itemId);
	}

	@ResponseStatus(HttpStatus.CREATED)
	@PostMapping("/carts/{subscriptionCartId}")
	public SingleResponseDto<Long> postSubscriptionItemCart(
		@PathVariable Long subscriptionCartId,
		@Positive @RequestParam int quantity,
		@RequestParam Long itemId,
		SubscriptionCartItemPostDto postDto
	) {

		cartService.addSubscriptionItem(
			subscriptionCartId,
			cartServiceMapper.toItem(itemId),
			cartServiceMapper.toSubscriptionInfo(postDto),
			quantity
		);

		return new SingleResponseDto<>(itemId);
	}

	@GetMapping("/carts/normal/{cartId}")
	public SingleResponseDto<CartResponseDto> getNormalCart(
		@PathVariable Long cartId
	) {
		NormalCart normalCart = cartService.findNormalCart(cartId);
		CartResponseDto cartResponseDto = cartResponseMapper.cartResponseDto(normalCart);

		return new SingleResponseDto<>(cartResponseDto);
	}

	@GetMapping("/carts/subscription/{cartId}")
	public SingleResponseDto<CartResponseDto> getSubscriptionCart(
		@PathVariable Long cartId
	) {

		SubscriptionCart subscriptionCart = cartService.findSubscriptionCart(cartId);
		CartResponseDto cartResponseDto = cartResponseMapper.cartResponseDto(subscriptionCart);

		return new SingleResponseDto<>(cartResponseDto);
	}

	@PatchMapping("/carts/normal/{cartId}")
	public SingleResponseDto<CartResponseDto> patchNormalCart(
		@PathVariable Long cartId,
		@RequestParam Long itemId
	) {

		NormalCart normalCart = cartService.correctNormalCart(cartId, cartServiceMapper.toItem(itemId));
		CartResponseDto cartResponseDto = cartResponseMapper.cartResponseDto(normalCart);

		return new SingleResponseDto<>(cartResponseDto);
	}

	@PatchMapping("/carts/subscription/{cartId}")
	public SingleResponseDto<CartResponseDto> patchSubscriptionCart(
		@PathVariable Long cartId,
		@RequestParam Long itemId
	) {

		NormalCart normalCart = cartService.correctNormalCart(cartId, cartServiceMapper.toItem(itemId));
		CartResponseDto cartResponseDto = cartResponseMapper.cartResponseDto(normalCart);

		return new SingleResponseDto<>(cartResponseDto);
	}
}

