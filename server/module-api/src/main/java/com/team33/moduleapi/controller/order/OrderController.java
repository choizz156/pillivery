package com.team33.moduleapi.controller.order;


import com.team33.modulecore.domain.cart.service.CartService;
import com.team33.modulecore.domain.cart.service.ItemCartService;
import com.team33.modulecore.domain.order.dto.OrderDetailResponse;
import com.team33.modulecore.domain.order.dto.OrderDto;
import com.team33.modulecore.domain.order.entity.Order;
import com.team33.modulecore.domain.order.entity.OrderItem;
import com.team33.modulecore.domain.order.service.OrderItemService;
import com.team33.modulecore.domain.order.service.OrderService;
import com.team33.modulecore.domain.user.service.UserService;
import com.team33.modulecore.global.response.SingleResponseDto;
import java.util.List;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@Validated
@CrossOrigin
@RequiredArgsConstructor
@RestController
@RequestMapping("/orders")
public class OrderController {

    private final OrderService orderService;
    private final OrderItemService orderItemService;
    private final ItemCartService itemCartService;
    private final CartService cartService;
    private final UserService userService;
//    private final ItemMapper itemMapper;

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/{userId}/single")
    public SingleResponseDto postSingleOrder(
        @PathVariable long userId,
        @RequestBody @Valid OrderDto.Post orderPostDto
    ) {
        List<OrderItem> orderItems = orderItemService.getOrderItemList(orderPostDto);
        Order order = orderService.callOrder(orderItems, orderPostDto, userId);
        orderService.creatOrderItem(order);

        return new SingleResponseDto<>(OrderDetailResponse.of(order));
    }

//    @PostMapping// 장바구니에서 주문요청을 하는 경우
//    public ResponseEntity postOrder(
//        @RequestParam(value = "subscription", defaultValue = "false") boolean subscription) {
//
//        User user = userService.getLoginUser();
//
//        List<OrderItem> orderItems = itemOrderMapper.itemCartsToItemOrders(
//            itemCartService.findItemCarts(user.getCart(), subscription, true), itemCartService);
//
//        cartService.refreshCart(user.getCart().getCartId(), subscription);
//
//        Order order = orderService.callOrder(orderItems, user);
//
//        return new ResponseEntity<>(new SingleResponseDto<>(
//            orderMapper.orderToOrderDetailResponseDto(order, itemMapper, itemOrderMapper)),
//            HttpStatus.OK);
//    }
//
//    @GetMapping // 로그인 한 유저의 일반 / 정기 주문 목록 불러오기
//    public ResponseEntity getOrders(
//        @Positive @RequestParam(value = "page", defaultValue = "1") int page,
//        @RequestParam(value = "subscription", defaultValue = "false") boolean subscription) {
//        Page<Order> pageOrders = orderService.findOrders(userService.getLoginUser(), page - 1,
//            subscription);
//
//        List<Order> orders = pageOrders.getContent();
//
//        return new ResponseEntity<>(new MultiResponseDto<>(
//            orderMapper.ordersToOrderSimpleResponseDtos(orders, itemMapper), pageOrders),
//            HttpStatus.OK);
//    }
//
//    @GetMapping("/subs") // 정기 구독 목록 불러오기
//    public ResponseEntity getSubsciptions(
//        @Positive @RequestParam(value = "page", defaultValue = "1") int page) {
//        Page<OrderItem> itemOrderPage = orderService.findAllSubs(userService.getLoginUser(),
//            page - 1);
//        List<OrderItem> orderItems = itemOrderPage.getContent();
//
//        return new ResponseEntity<>(new MultiResponseDto<>(
//            itemOrderMapper.itemOrdersToSubResponses(orderItems, itemMapper), itemOrderPage),
//            HttpStatus.OK);
//    }
//
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
//    @GetMapping("/{order-id}") // 특정 주문의 상세 내역 확인
//    public ResponseEntity getOrder(@PathVariable("order-id") @Positive long orderId) {
//
//        Order order = orderService.findOrder(orderId);
//
//        return new ResponseEntity<>(new SingleResponseDto<>(
//            orderMapper.orderToOrderDetailResponseDto(order, itemMapper, itemOrderMapper)),
//            HttpStatus.OK);
//    }
//
//    @DeleteMapping("/{order-id}") // 특정 주문 취소
//    public ResponseEntity cancelOrder(@PathVariable("order-id") @Positive long orderId) {
//        orderService.cancelOrder(orderId);
//
//        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
//    }
}
