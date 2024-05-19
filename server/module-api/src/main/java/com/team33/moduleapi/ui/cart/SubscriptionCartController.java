package com.team33.moduleapi.ui.cart;

import javax.validation.constraints.Min;

import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.team33.moduleapi.dto.SingleResponseDto;
import com.team33.moduleapi.ui.cart.dto.CartResponseDto;
import com.team33.moduleapi.ui.cart.dto.SubscriptionCartItemPostDto;
import com.team33.moduleapi.ui.cart.mapper.CartResponseMapper;
import com.team33.moduleapi.ui.cart.mapper.CartServiceMapper;
import com.team33.modulecore.cart.SubscriptionContext;
import com.team33.modulecore.cart.application.SubscriptionCartService;
import com.team33.modulecore.cart.domain.entity.Cart;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/carts/subscription/")
public class SubscriptionCartController {

	private final SubscriptionCartService subscriptionCartService;
	private final CartServiceMapper cartServiceMapper;
	private final CartResponseMapper cartResponseMapper;

	@GetMapping("/{cartId}")
	public SingleResponseDto<CartResponseDto> getSubscriptionCart(
		@PathVariable Long cartId
	) {

		Cart cart = subscriptionCartService.findCart(cartId);
		CartResponseDto cartResponseDto = cartResponseMapper.toCartSubscriptionResponseDto(cart);

		return new SingleResponseDto<>(cartResponseDto);
	}

	@ResponseStatus(HttpStatus.CREATED)
	@PostMapping("/{cartId}")
	public SingleResponseDto<Long> postSubscriptionItemCart(
		@PathVariable Long cartId,
		SubscriptionCartItemPostDto postDto
	) {
		SubscriptionContext subscriptionContext = cartServiceMapper.toSubscriptionContext(postDto);

		subscriptionCartService.addItem(cartId, subscriptionContext);

		return new SingleResponseDto<>(postDto.getItemId());
	}

	@ResponseStatus(HttpStatus.NO_CONTENT)
	@DeleteMapping("/{cartId}")
	public void patchSubscriptionCart(
		@PathVariable Long cartId,
		@RequestParam Long itemId
	) {
		subscriptionCartService.removeCartItem(cartId, cartServiceMapper.toItem(itemId));
	}

	@ResponseStatus(HttpStatus.NO_CONTENT)
	@PatchMapping("/{cartId}/quantity")
	public void patchItemQuantity(
		@PathVariable Long cartId,
		@RequestParam Long itemId,
		@Min(1) @RequestParam int quantity
	) {
		subscriptionCartService.changeQuantity(cartId, cartServiceMapper.toItem(itemId), quantity);
	}

	@ResponseStatus(HttpStatus.NO_CONTENT)
	@PatchMapping("/{cartId}/period")
	public void patchPeriod(
		@PathVariable Long cartId,
		@RequestParam Long itemId,
		@Min(30) @RequestParam int period
	) {
		subscriptionCartService.changePeriod(cartId, cartServiceMapper.toItem(itemId), period);
	}

}

