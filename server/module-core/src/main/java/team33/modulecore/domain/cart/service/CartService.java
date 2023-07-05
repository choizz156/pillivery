package team33.modulecore.domain.cart.service;


import java.util.List;
import java.util.Optional;
import javax.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import team33.modulecore.domain.cart.entity.Cart;
import team33.modulecore.domain.cart.entity.ItemCart;
import team33.modulecore.domain.cart.repository.CartRepository;
import team33.modulecore.domain.user.entity.User;
import team33.modulecore.domain.user.service.UserService;
import team33.modulecore.global.exception.BusinessLogicException;
import team33.modulecore.global.exception.ExceptionCode;

@Service
@Transactional
@RequiredArgsConstructor
public class CartService {

    private final CartRepository cartRepository;
    private final ItemCartService itemCartService;
    private final UserService userService;

    public void refreshCart(long cartId, boolean subscription) { // 가격과 아이템 종류 갱신
        Cart cart = findVerifiedCart(cartId);

        if(subscription) {
            cart.setSubTotalPrice(countTotalPrice(cartId, subscription));
            cart.setSubTotalItems(countTotalItems(cartId, subscription));
        } else {
            cart.setTotalPrice(countTotalPrice(cartId, subscription));
            cart.setTotalItems(countTotalItems(cartId, subscription));
        }

        cartRepository.save(cart);
    }

    public Cart findMyCart() {
        User user = userService.getLoginUser();
        return cartRepository.findByUser(user);
    }

    public Cart findCart(long cartId) {
        Cart findCart = findVerifiedCart(cartId);
        return findCart;
    }

    public Cart findVerifiedCart(long cartId) {
        Optional<Cart> optionalCart = cartRepository.findById(cartId);
        Cart findCart = optionalCart.orElseThrow(
                () -> new BusinessLogicException(ExceptionCode.CART_NOT_FOUND));
        return findCart;
    }

    public int countTotalDiscountPrice(long cartId, boolean subscription) {
        Cart cart = findVerifiedCart(cartId);
        List<ItemCart> itemCarts = itemCartService.findItemCarts(cart, subscription, true);

        if(itemCarts == null) return 0;

        int totalDiscountPrice = 0;

        for(ItemCart itemCart : itemCarts) {
            int quantity = itemCart.getItem().getPrice();
            int price = itemCart.getQuantity();
            int discountRate = itemCart.getItem().getDiscountRate();

            totalDiscountPrice += (quantity * price * discountRate/100);
        }

        return totalDiscountPrice;
    }

    private int countTotalPrice(long cartId, boolean subscription) {
        Cart cart = findVerifiedCart(cartId);
        List<ItemCart> itemCarts = itemCartService.findItemCarts(cart, subscription, true);

        if(itemCarts == null) return 0;

        int totalPrice = 0;

        for(ItemCart itemCart : itemCarts) {
            int quantity = itemCart.getItem().getPrice();
            int price = itemCart.getQuantity();
            totalPrice += (quantity * price);
        }

        return totalPrice;
    }

    private int countTotalItems(long cartId, boolean subscription) {
        Cart cart = findVerifiedCart(cartId);
        return itemCartService.findItemCarts(cart, subscription, true).size();
    }
}
