package com.team33.moduleapi.controller.cart;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.team33.moduleapi.dto.SingleResponseDto;
import com.team33.moduleapi.ui.cart.CartServiceMapper;
import com.team33.modulecore.cart.application.CartService;
import com.team33.modulecore.cart.domain.NormalCart;
import com.team33.modulecore.cart.domain.SubscriptionCart;
import com.team33.modulecore.itemcart.dto.SubscriptionItemPostDto;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Validated
@RequiredArgsConstructor
@RestController
public class CartController {

	private final CartService cartService;
	// private final CartMapper cartMapper;
	// private final ItemCartService itemCartService;
	// private final ItemMapper itemMapper;
	// private final ItemCartMapper itemCartMapper;

	@ResponseStatus(HttpStatus.CREATED)
	@PostMapping("/carts/{normalCartId}") // 장바구니 담기
	public SingleResponseDto<Long> postNormalItemCart(
		@NotNull @PathVariable Long normalCartId,
		@NotNull @Positive @RequestParam int quantity,
		@NotNull @RequestParam Long itemId
	) {
		cartService.addItem(normalCartId, itemId, quantity);

		return new SingleResponseDto<>(itemId);
	}

	@ResponseStatus(HttpStatus.CREATED)
	@PostMapping("/carts/{subscriptionCartId}") // 장바구니 담기
	public SingleResponseDto<Long> postSubscriptionItemCart(
		@NotNull @PathVariable Long subscriptionCartId,
		@NotNull @Positive @RequestParam int quantity,
		@NotNull @RequestParam Long itemId,
		SubscriptionItemPostDto postDto
	) {

		cartService.addSubscriptionItem(
			subscriptionCartId,
			itemId,
			CartServiceMapper.toSubscriptionInfo(postDto),
			quantity
		);

		return new SingleResponseDto<>(itemId);
	}

	@GetMapping("/users/{userId}/carts/normal")
	public ResponseEntity getNormalCart(
		@PathVariable Long userId
	) {
		NormalCart normalCart = cartService.findNormalCart(userId);
		return null;
	}

	@GetMapping("/users/{userId}/carts/subscription")
	public ResponseEntity getSubscirptionCart(
		@PathVariable Long userId
	) {

		SubscriptionCart subscriptionCart = cartService.findSubscriptionCart(userId);
		return null;
	}
}

