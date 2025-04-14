package com.team33.moduleapi.api.cart;

import javax.validation.constraints.Min;

import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.team33.moduleapi.api.cart.dto.CartResponseDto;
import com.team33.moduleapi.api.cart.dto.SubscriptionCartItemPostDto;
import com.team33.moduleapi.api.cart.mapper.CartResponseMapper;
import com.team33.moduleapi.api.cart.mapper.CartServiceMapper;
import com.team33.moduleapi.response.SingleResponseDto;
import com.team33.modulecore.core.cart.application.CartKeySupplier;
import com.team33.modulecore.core.cart.application.MemoryCartService;
import com.team33.modulecore.core.cart.application.SubscriptionCartItemService;
import com.team33.modulecore.core.cart.dto.SubscriptionContext;
import com.team33.modulecore.core.cart.vo.SubscriptionCartVO;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/carts/subscription")
public class SubscriptionCartController {

	private final SubscriptionCartItemService subscriptionCartService;
	private final CartServiceMapper cartServiceMapper;
	private final CartResponseMapper cartResponseMapper;
	private final MemoryCartService memoryCartService;

	@GetMapping("/{cartId}")
	public SingleResponseDto<CartResponseDto> getSubscriptionCart(
		@PathVariable Long cartId
	) {
		SubscriptionCartVO subscriptionCartVO = subscriptionCartService.findCart(CartKeySupplier.from(cartId), cartId);
		CartResponseDto cartResponseDto = cartResponseMapper.toCartSubscriptionResponseDto(subscriptionCartVO);

		return new SingleResponseDto<>(cartResponseDto);
	}

	@ResponseStatus(HttpStatus.CREATED)
	@PostMapping("/{cartId}")
	public SingleResponseDto<Long> postSubscriptionItemCart(
		@PathVariable Long cartId,
		@RequestBody SubscriptionCartItemPostDto postDto
	) {
		SubscriptionContext subscriptionContext = cartServiceMapper.toSubscriptionContext(postDto);
		memoryCartService.addSubscriptionItem(CartKeySupplier.from(cartId), subscriptionContext);

		return new SingleResponseDto<>(postDto.getItemId());
	}

	@ResponseStatus(HttpStatus.NO_CONTENT)
	@DeleteMapping("/{cartId}")
	public void patchSubscriptionCart(
		@PathVariable Long cartId,
		@RequestParam Long itemId
	) {
		memoryCartService.deleteCartItem(CartKeySupplier.from(cartId), itemId, SubscriptionCartVO.class);
	}

	@ResponseStatus(HttpStatus.NO_CONTENT)
	@PatchMapping("/{cartId}/quantity")
	public void patchItemQuantity(
		@PathVariable Long cartId,
		@Min(1) @RequestParam int quantity,
		@RequestParam Long itemId
	) {
		memoryCartService.changeQuantity(CartKeySupplier.from(cartId), itemId, quantity);
	}

	@ResponseStatus(HttpStatus.NO_CONTENT)
	@PatchMapping("/{cartId}/period")
	public void patchPeriod(
		@PathVariable Long cartId,
		@RequestParam Long itemId,
		@Min(30) @RequestParam int period
	) {
		memoryCartService.changePeriod(CartKeySupplier.from(cartId), itemId, period);
	}

}

