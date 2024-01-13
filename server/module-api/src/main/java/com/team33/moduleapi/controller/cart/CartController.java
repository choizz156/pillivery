package com.team33.moduleapi.controller.cart;

import com.team33.modulecore.domain.cart.entity.Cart;
import com.team33.modulecore.domain.cart.mapper.CartMapper;
import com.team33.modulecore.domain.cart.mapper.ItemCartMapper;
import com.team33.modulecore.domain.cart.service.CartService;
import com.team33.modulecore.domain.cart.service.ItemCartService;
import com.team33.modulecore.domain.item.mapper.ItemMapper;
import com.team33.moduleapi.response.SingleResponseDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@Validated
@CrossOrigin
@RestController
@RequiredArgsConstructor
@RequestMapping("/carts")
public class CartController {

    private final CartService cartService;
    private final CartMapper cartMapper;
    private final ItemCartService itemCartService;
    private final ItemMapper itemMapper;
    private final ItemCartMapper itemCartMapper;

    @GetMapping
    public ResponseEntity getCart(@RequestParam(value="subscription", defaultValue="false") boolean subscription) {

        Cart cart = cartService.findMyCart();

        return new ResponseEntity<>(new SingleResponseDto<>(cartMapper.cartToCartResponseDto(
                        cart, cartService, subscription, itemCartService, itemMapper, itemCartMapper)), HttpStatus.OK);
    }

}
