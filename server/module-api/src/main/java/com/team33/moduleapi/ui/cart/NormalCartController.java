package com.team33.moduleapi.ui.cart;

import javax.validation.constraints.Min;

import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.team33.moduleapi.dto.SingleResponseDto;
import com.team33.moduleapi.ui.cart.dto.CartResponseDto;
import com.team33.moduleapi.ui.cart.mapper.CartResponseMapper;
import com.team33.moduleapi.ui.cart.mapper.CartServiceMapper;
import com.team33.modulecore.cart.application.NormalCartService;
import com.team33.modulecore.cart.domain.entity.Cart;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Validated
@RequiredArgsConstructor
@RestController
public class NormalCartController {

	private final NormalCartService normalCartService;
	private final CartServiceMapper cartServiceMapper;
	private final CartResponseMapper cartResponseMapper;

	@GetMapping("/carts/normal/{cartId}")
	public SingleResponseDto<CartResponseDto> getNormalCart(
		@PathVariable Long cartId
	) {
		Cart cart = normalCartService.findCart(cartId);
		CartResponseDto cartResponseDto = cartResponseMapper.cartNormalResponseDto(cart);

		return new SingleResponseDto<>(cartResponseDto);
	}

	@ResponseStatus(HttpStatus.CREATED)
	@PostMapping("/carts/{normalCartId}")
	public SingleResponseDto<Long> postNormalItemCart(
		@PathVariable Long normalCartId,
		@Min(1) @RequestParam int quantity,
		@RequestParam Long itemId
	) {
		normalCartService.addItem(normalCartId, cartServiceMapper.toItem(itemId), quantity);

		return new SingleResponseDto<>(itemId);
	}

	@ResponseStatus(HttpStatus.NO_CONTENT)
	@DeleteMapping("/carts/normal/{cartId}")
	public void removeNormalCart(
		@PathVariable Long cartId,
		@RequestParam Long itemId
	) {
		normalCartService.removeCartItem(cartId, cartServiceMapper.toItem(itemId));
	}

	@ResponseStatus(HttpStatus.NO_CONTENT)
	@PatchMapping("/carts/normal/{cartId}")
	public void changeItemQauntity(
		@PathVariable Long cartId,
		@Min(1)@RequestParam int quantity,
		@RequestParam Long itemId
	) {
		normalCartService.changeQuantity(cartId, cartServiceMapper.toItem(itemId), quantity);
	}

}

