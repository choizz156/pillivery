package com.team33.modulecore.itemcart.application;


import com.team33.modulecore.cart.domain.Cart;
import com.team33.modulecore.itemcart.domain.ItemCart;
import com.team33.modulecore.cart.repository.CartRepository;
import com.team33.modulecore.itemcart.repository.ItemCartRepository;
import com.team33.modulecore.user.domain.User;
import com.team33.modulecore.user.domain.repository.UserRepository;
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
public class ItemCartService {

    private final ItemCartRepository itemCartRepository;
    private final CartRepository cartRepository;
    private final UserRepository userRepository;

    public ItemCart addItemCart(ItemCart itemCart) {
        ItemCart findItemCart = checkItemCart(itemCart);

        if (findItemCart == null) {
            createItemCart(itemCart); // 상품이 장바구니에 없는 경우 장바구니에 추가
            return itemCart;
        } else {
            findItemCart.addQuantity(itemCart.getQuantity()); // 상품이 장바구니에 이미 있는 경우 수량만 증가
            itemCartRepository.save(findItemCart);
            return findItemCart;
        }
    }

    public ItemCart createItemCart(ItemCart itemCart) { // 상품이 장바구니에 없는 경우
        return itemCartRepository.save(itemCart);
    }

    public ItemCart checkItemCart(ItemCart itemCart) { // 장바구니에 특정 아이템이 이미 담겨있는지 확인
        return itemCartRepository.findByCartAndItemAndOrderItemInfoSubscription(
            itemCart.getCart(), itemCart.getItem(), itemCart.getSubscription());
    }

    public ItemCart findItemCart(long itemCartId) {
        ItemCart findItemCart = findVerifiedItemCart(itemCartId);
        return findItemCart;
    }

    public ItemCart updownItemCart(long itemCartId, int upDown) { // 수량 변경( +1 or -1)
        ItemCart itemCart = findVerifiedItemCart(itemCartId);
        itemCart.addQuantity(upDown);
        itemCartRepository.save(itemCart);
        return itemCart;
    }

    public ItemCart excludeItemCart(long itemCartId, boolean buyNow) { // 아이템 체크 및 해제
        ItemCart itemCart = findVerifiedItemCart(itemCartId);
        itemCart.setBuyNow(buyNow);
        return itemCartRepository.save(itemCart);
    }

//    public long deleteItemCart(long itemCartId) { // 장바구니 항목 삭제
//        ItemCart itemCart = findVerifiedItemCart(itemCartId);
//        long cartId = itemCart.getCart().getCartId(); // 장바구니 리프레시를 위해 카트 정보 확인
//        itemCartRepository.delete(itemCart);
//        return cartId;
//    }

    public List<ItemCart> findItemCarts(Cart cart, boolean subscription) { // 장바구니 목록 조회
        return itemCartRepository.findAllByCartAndOrderItemInfoSubscription(cart, subscription);
        // subscription - true 정기구독, false 일반
    }

    public List<ItemCart> findItemCarts(Cart cart, boolean subscription,
        boolean buyNow) { // 금액 합계, 주문
        return itemCartRepository.findAllByCartAndOrderItemInfoSubscriptionAndBuyNow(cart, subscription, buyNow);
        // subscription - true 정기구독, false 일반
        // buyNow - true 체크박스 활성화, false 체크박스 비활성화
    }

    public List<ItemCart> findItemCarts(long userId, boolean subscription) {
        User user = getUser(userId);
        Cart cart = getCart(user);
        return getItemCarts(subscription, cart);
    }

    public ItemCart findVerifiedItemCart(long itemCartId) {
        Optional<ItemCart> optionalItemCart = itemCartRepository.findById(itemCartId);
        ItemCart findItemCart = optionalItemCart.orElseThrow(() ->
            new BusinessLogicException(ExceptionCode.ITEMCART_NOT_FOUND));
        return findItemCart;
    }

    private Cart getCart(User user) {
        return cartRepository.findById(user.getCart().getCartId())
            .orElseThrow(() -> new BusinessLogicException(ExceptionCode.CART_NOT_FOUND));
    }

    private User getUser(long userId) {
        return userRepository.findById(userId).orElseThrow(
            () -> new BusinessLogicException(ExceptionCode.USER_NOT_FOUND)
        );
    }

    private List<ItemCart> getItemCarts(boolean subscription, Cart cart) {
        List<ItemCart> itemCarts =
            itemCartRepository.findAllByCartAndOrderItemInfoSubscriptionAndBuyNow(cart, subscription, true);

        if (itemCarts.isEmpty()) {
            throw new BusinessLogicException(ExceptionCode.ITEMCART_NOT_FOUND);
        }
        return itemCarts;
    }


}