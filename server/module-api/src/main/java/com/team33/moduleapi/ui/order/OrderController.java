package com.team33.moduleapi.ui.order;


import com.team33.moduleapi.dto.MultiResponseDto;
import com.team33.moduleapi.dto.SingleResponseDto;
import com.team33.modulecore.cart.application.CartService;
import com.team33.modulecore.common.PageDto;
import com.team33.modulecore.itemcart.application.ItemCartService;
import com.team33.modulecore.itemcart.domain.ItemCart;
import com.team33.modulecore.order.application.OrderQueryService;
import com.team33.modulecore.order.application.OrderService;
import com.team33.modulecore.order.domain.Order;
import com.team33.modulecore.order.dto.OrderDetailResponse;
import com.team33.modulecore.order.dto.OrderPageRequest;
import com.team33.modulecore.order.dto.OrderPostDto;
import com.team33.modulecore.order.dto.OrderSimpleResponse;
import com.team33.modulecore.orderitem.application.OrderItemService;
import com.team33.modulecore.orderitem.domain.OrderItem;
import com.team33.modulecore.orderitem.dto.OrderItemServiceDto;
import com.team33.modulecore.orderitem.dto.OrderItemSimpleResponse;
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
import org.springframework.web.bind.annotation.GetMapping;
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
@Validated
@RequiredArgsConstructor
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
     */
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/single")
    public SingleResponseDto<?> postSingleOrder(
        @NotNull @RequestParam Long userId,
        @RequestBody @Valid OrderPostDto orderPostDto
    ) {
        OrderItemServiceDto dto = OrderItemServiceDto.to(orderPostDto);
        List<OrderItem> orderItems =
            orderItemService.getOrderItemSingle(dto);
        Order order = orderService.callOrder(orderItems, orderPostDto.isSubscription(), userId);

        return new SingleResponseDto<>(OrderDetailResponse.of(order));
    }

    /**
     * 장바구니에서 주문 요청을 하는 경우의 주문을 생성합니다.
     */
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/cart")// 장바구니에서 주문요청을 하는 경우
    public SingleResponseDto postOrderInCart(
        @NotNull @RequestParam Long userId,
        @RequestParam(value = "subscription") Boolean subscription
    ) {
        List<ItemCart> itemCarts = itemCartService.findItemCarts(userId, subscription);
        List<OrderItem> orderItems = orderItemService.getOrderItemsInCart(itemCarts);
        cartService.refreshCart(itemCarts, subscription);

        Order order = orderService.callOrder(orderItems, subscription, userId);

        return new SingleResponseDto<>(OrderDetailResponse.of(order));
    }

    /**
     * 주문(일반, 정기) 목록을 불러 옵니다.(상세 x)
     */
    @GetMapping
    public MultiResponseDto<?> getOrders(
        @NotNull @RequestParam Long userId,
        PageDto pageDto
    ) {

        OrderPageRequest orderPageRequest = OrderPageRequest.of(pageDto);
        Page<Order> allOrders = orderQueryService.findAllOrders(userId, orderPageRequest);
        List<Order> orders = allOrders.getContent();
        List<OrderSimpleResponse> ordersDto = OrderSimpleResponse.toList(orders);

        return new MultiResponseDto<>(orders, allOrders);
    }

    @GetMapping("/subscriptions") // 정기 구독 목록 불러오기
    public MultiResponseDto<?> getSubscriptionsOrder(
        @NotNull @RequestParam Long userId,
        PageDto pageDto
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

//    @GetMapping("/{order-id}") // 특정 주문의 상세 내역 확인
//    public ResponseEntity getOrder(
//        @NotNull @RequestParam Long userId,
//        @Positive @PathVariable("order-id")  long orderId) {
//
//        Order order = orderService.findOrder(orderId);
//
//        return new ResponseEntity<>(new SingleResponseDto<>(
//            orderMapper.orderToOrderDetailResponseDto(order, itemMapper, itemOrderMapper)),
//            HttpStatus.OK);
//    }

//    @PatchMapping("/subs/{itemOrder-id}") // 정기 구독 아이템의 수량 변경
//    public ResponseEntity changeQuantity(@PathVariable("itemOrder-id") long itemOrderId,
//        @RequestParam(value = "upDown") int upDown) {
//
//        OrderItem orderItem = orderItemService.changeSubQuantity(itemOrderId, upDown);
//
//        return new ResponseEntity<>(new SingleResponseDto<>(
//            itemOrderMapper.itemOrderToSubResponse(orderItem, itemMapper)), HttpStatus.OK);
//    }
//
//
//    @DeleteMapping("/{order-id}") // 특정 주문 취소
//    public ResponseEntity cancelOrder(@PathVariable("order-id") @Positive long orderId) {
//        orderService.cancelOrder(orderId);
//
//        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
//    }
}
