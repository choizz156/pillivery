package com.team33.moduleapi.ui.order;


import com.team33.moduleapi.dto.MultiResponseDto;
import com.team33.moduleapi.dto.SingleResponseDto;
import com.team33.modulecore.cart.application.CartService;
import com.team33.modulecore.common.OrderPageDto;
import com.team33.modulecore.itemcart.application.ItemCartService;
import com.team33.modulecore.itemcart.domain.ItemCart;
import com.team33.modulecore.order.application.OrderItemService;
import com.team33.modulecore.order.application.OrderQueryService;
import com.team33.modulecore.order.application.OrderService;
import com.team33.modulecore.order.domain.Order;
import com.team33.modulecore.order.domain.OrderItem;
import com.team33.modulecore.order.dto.OrderDetailResponse;
import com.team33.modulecore.order.dto.OrderItemServiceDto;
import com.team33.modulecore.order.dto.OrderItemSimpleResponse;
import com.team33.modulecore.order.dto.OrderPageRequest;
import com.team33.modulecore.order.dto.OrderPostDto;
import com.team33.modulecore.order.dto.OrderSimpleResponse;
import com.team33.modulecore.user.application.UserService;
import java.util.List;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
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

/**
 * The type Order controller.
 */
@Slf4j
@RequiredArgsConstructor
@Validated
@RestController
@RequestMapping("/orders")
public class OrderController {

    private final OrderService orderService;
    private final OrderQueryService orderQueryService;
    private final OrderItemService orderItemService;
    private final ItemCartService itemCartService;
    private final CartService cartService;
    private final UserService userService;
//    private final ItemMapper itemMapper;

    /**
     * 단건 주문 정보를 생성합니다.
     *
     * @param userId       the user id
     * @param orderPostDto the order post dto
     * @return the single response dto
     */
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/single")
    public SingleResponseDto<?> postSingleOrder(
        @RequestParam Long userId,
        @Valid @RequestBody OrderPostDto orderPostDto
    ) {
        OrderItemServiceDto dto = OrderItemServiceDto.to(orderPostDto);

        List<OrderItem> orderItems =
            orderItemService.getOrderItemSingle(dto);

        Order order = orderService.callOrder(orderItems, orderPostDto.isSubscription(), userId);

        return new SingleResponseDto<>(OrderDetailResponse.of(order));
    }

    /**
     * 장바구니에서 주문 요청을 하는 경우의 주문을 생성합니다.
     *
     * @param userId       the user id
     * @param subscription the subscription
     * @return the single response dto
     */
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/cart")// 장바구니에서 주문요청을 하는 경우
    public SingleResponseDto<?> postOrderInCart(
        @RequestParam Long userId,
        @RequestParam(value = "subscription") boolean subscription
    ) {
        List<ItemCart> itemCarts = itemCartService.findItemCarts(userId, subscription);
        List<OrderItem> orderItems = orderItemService.getOrderItemsInCart(itemCarts);

        cartService.refreshCart(itemCarts, subscription);

        Order order = orderService.callOrder(orderItems, subscription, userId);

        return new SingleResponseDto<>(OrderDetailResponse.of(order));
    }

    /**
     * 주문(일반, 정기) 목록을 불러 옵니다.(상세 x)
     *
     * @param userId  the user id
     * @param pageDto the page dto
     * @return the orders
     */
    @GetMapping
    public MultiResponseDto<?> getOrders(
        @RequestParam Long userId,
        OrderPageDto pageDto
    ) {
        OrderPageRequest orderPageRequest = OrderPageRequest.of(pageDto);

        Page<Order> allOrders = orderQueryService.findAllOrders(userId, orderPageRequest);

        List<Order> orders = allOrders.getContent();

        List<OrderSimpleResponse> ordersDto = OrderSimpleResponse.toList(orders);

        return new MultiResponseDto<>(ordersDto, allOrders);
    }

    /**
     * Gets subscriptions order.
     *
     * @param userId  the user id
     * @param pageDto the page dto
     * @return the subscriptions order
     */
    @GetMapping("/subscriptions")
    public MultiResponseDto<?> getSubscriptionsOrder(
        @RequestParam Long userId,
        OrderPageDto pageDto
    ) {
        OrderPageRequest orderPageRequest = OrderPageRequest.of(pageDto);

        List<OrderItemSimpleResponse> allSubscriptions =
            orderQueryService.findAllSubscriptions(userId, orderPageRequest);

        return new MultiResponseDto<>(
            allSubscriptions,
            new PageImpl<>(
                allSubscriptions,
                PageRequest.of(pageDto.getPage() - 1, pageDto.getSize()),
                allSubscriptions.size()
            )
        );
    }

    /**
     * 특정 주문 상세 내역을 확인
     */
    @GetMapping("/{orderId}")
    public SingleResponseDto<?> getOrder(
        @RequestParam Long userId,
        @PathVariable Long orderId
    ) {
        Order order = orderQueryService.findOrder(orderId);
        OrderDetailResponse orderDetailResponse = OrderDetailResponse.of(order);

        return new SingleResponseDto<>(orderDetailResponse);
    }

    /**
     * Change quantity single response dto.
     */
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PatchMapping("/subscription/{orderId}") // 정기 구독 아이템의 수량 변경
    public void changeSubscriptionItemQuantity(
        @PathVariable Long orderId,
        @RequestParam Long orderItemId,
        @RequestParam int quantity
    ) {
        orderService.changeSubscriptionItemQuantity(orderId, orderItemId, quantity);
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("/{orderId}") // 특정 주문 취소
    public void cancelOrder(@NotNull @PathVariable Long orderId) {
        orderService.cancelOrder(orderId);
    }
}
