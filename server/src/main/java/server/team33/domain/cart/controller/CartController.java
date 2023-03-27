package server.team33.domain.cart.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import server.team33.domain.cart.entity.Cart;
import server.team33.domain.cart.mapper.CartMapper;
import server.team33.domain.cart.mapper.ItemCartMapper;
import server.team33.domain.cart.service.CartService;
import server.team33.domain.cart.service.ItemCartService;
import server.team33.domain.item.mapper.ItemMapper;
import server.team33.global.response.SingleResponseDto;

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
