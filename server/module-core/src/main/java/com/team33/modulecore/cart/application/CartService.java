package com.team33.modulecore.cart.application;


import com.team33.modulecore.cart.domain.Cart;
import com.team33.modulecore.itemcart.application.ItemCartService;
import com.team33.modulecore.itemcart.domain.ItemCart;
import com.team33.modulecore.cart.repository.CartRepository;
import com.team33.modulecore.user.application.UserService;
import com.team33.modulecore.exception.BusinessLogicException;
import com.team33.modulecore.exception.ExceptionCode;
import java.util.List;
import java.util.Optional;
import javax.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;


@RequiredArgsConstructor
@Transactional
@Service
public class CartService {

    private final CartRepository cartRepository;
    private final ItemCartService itemCartService;
    private final UserService userService;


    public void refreshCart(List<ItemCart> itemCarts, boolean subscription) { // 가격과 아이템 종류 갱신
        itemCarts.forEach(ic -> {
            Cart cart = findCart(ic.getCart().getCartId());
            calculatePriceAndItemSize(subscription, itemCarts, cart);
            cartRepository.save(cart);
        });
    }

//    public Cart findMyCart() {
//        User user = userService.getLoginUser();
//        return cartRepository.findByUser(user);
//    }

    public Cart findCart(long cartId) {
        Optional<Cart> optionalCart = cartRepository.findById(cartId);
        return optionalCart.orElseThrow(
            () -> new BusinessLogicException(ExceptionCode.CART_NOT_FOUND));
    }

    public int countTotalDiscountPrice(long cartId, boolean subscription) {
        Cart cart = findCart(cartId);
        List<ItemCart> itemCarts = itemCartService.findItemCarts(cart, subscription, true);

        if (itemCarts == null) {
            return 0;
        }

        int totalDiscountPrice = 0;

        for (ItemCart itemCart : itemCarts) {
            int quantity = itemCart.getItem().getPrice();
            int price = itemCart.getQuantity();
            int discountRate = itemCart.getItem().getDiscountRate();

            totalDiscountPrice += (quantity * price * discountRate / 100);
        }

        return totalDiscountPrice;
    }


    private void calculatePriceAndItemSize(
        boolean subscription,
        List<ItemCart> itemCarts,
        Cart cart
    ) {
        int totalPrice = countTotalPrice(itemCarts);
        int totalItems = itemCarts.size();

        if (subscription) {
            cart.changeSubTotalPrice(totalPrice);
            cart.changeSubTotalItems(totalItems);
            return;
        }

        cart.changeTotalPrice(totalPrice);
        cart.changeTotalItems(totalItems);
    }

    private int countTotalPrice(List<ItemCart> itemCarts) {

        if (itemCarts.isEmpty()) {
            return 0;
        }
        int totalPrice = 0;

        for (ItemCart itemCart : itemCarts) {
            int quantity = itemCart.getItem().getPrice();
            int price = itemCart.getQuantity();
            totalPrice += (quantity * price);
        }

        return totalPrice;
    }
}
