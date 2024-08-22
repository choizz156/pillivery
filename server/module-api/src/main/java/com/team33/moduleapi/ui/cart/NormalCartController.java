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

import com.team33.moduleapi.response.SingleResponseDto;
import com.team33.moduleapi.ui.cart.dto.CartResponseDto;
import com.team33.moduleapi.ui.cart.mapper.CartResponseMapper;
import com.team33.moduleapi.ui.cart.mapper.CartServiceMapper;
import com.team33.modulecore.core.cart.application.CartKeySupplier;
import com.team33.modulecore.core.cart.application.MemoryCartClient;
import com.team33.modulecore.core.cart.application.NormalCartItemService;
import com.team33.modulecore.core.cart.domain.NormalCartVO;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Validated
@RequiredArgsConstructor
@RequestMapping("/api/carts/normal")
@RestController
public class NormalCartController {

	private final NormalCartItemService normalCartItemService;
	private final CartServiceMapper cartServiceMapper;
	private final CartResponseMapper cartResponseMapper;
	private final MemoryCartClient memoryCartClient;

	@GetMapping("/{cartId}")
	public SingleResponseDto<CartResponseDto> getNormalCart(
		@PathVariable Long cartId
	) {
		NormalCartVO normalCart = normalCartItemService.findCart(CartKeySupplier.from(cartId), cartId);
		CartResponseDto cartResponseDto = cartResponseMapper.cartNormalResponseDto(normalCart);

		return new SingleResponseDto<>(cartResponseDto);
	}

	@ResponseStatus(HttpStatus.CREATED)
	@PostMapping("/{cartId}")
	public SingleResponseDto<Long> postNormalItemCart(
		@PathVariable Long cartId,
		@Min(1) @RequestParam int quantity,
		@RequestParam Long itemId
	) {
		memoryCartClient.addNormalItem(CartKeySupplier.from(cartId), cartServiceMapper.toItemVO(itemId), quantity);

		return new SingleResponseDto<>(itemId);
	}

	@ResponseStatus(HttpStatus.NO_CONTENT)
	@DeleteMapping("/{cartId}")
	public void removeNormalCart(
		@PathVariable Long cartId,
		@RequestParam Long itemId
	) {
		memoryCartClient.deleteCartItem(CartKeySupplier.from(cartId), itemId);
	}

	@ResponseStatus(HttpStatus.NO_CONTENT)
	@PatchMapping("/{cartId}")
	public void changeItemQauntity(
		@PathVariable Long cartId,
		@Min(1) @RequestParam int quantity,
		@RequestParam Long itemId
	) {
		memoryCartClient.changeQuantity(CartKeySupplier.from(cartId), itemId, quantity);
	}
}

