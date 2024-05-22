//package com.team33.moduleapi.controller.Scheduler;
//
//import com.team33.modulecore.domain.item.mapper.ItemMapper;
//import com.team33.modulecore.domain.order.dto.OrderItemDto.SubResponse;
//import com.team33.modulecore.domain.order.entity.OrderItem;
//import com.team33.modulecore.domain.order.entity.Order;
//import com.team33.modulecore.domain.order.mapper.ItemOrderMapper;
//import com.team33.modulecore.domain.order.service.OrderItemService;
//import com.team33.modulecore.domain.order.service.OrderCreateService;
//import com.team33.moduleapi.response.SingleResponseDto;
//import com.team33.modulequartz.subscription.service.SubscriptionService;
//import java.time.ZonedDateTime;
//import java.util.List;
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.http.HttpStatus;
//import org.springframework.web.bind.annotation.DeleteMapping;
//import org.springframework.web.bind.annotation.GetMapping;
//import org.springframework.web.bind.annotation.PatchMapping;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RequestParam;
//import org.springframework.web.bind.annotation.ResponseStatus;
//import org.springframework.web.bind.annotation.RestController;
//
//@Slf4j
//@RequiredArgsConstructor
//@RequestMapping("/schedule")
//@RestController
//public class ScheduleController {
//
//    private final SubscriptionService subscriptionService;
//    private final OrderCreateService orderService;
//    private final OrderItemService orderItemService;
//    private final ItemOrderMapper itemOrderMapper;
//    private final ItemMapper itemMapper;
//
//    private static final String SUCCESS_SHCHDULE = "스케쥴 구성 완료";
//
//
//    @ResponseStatus(HttpStatus.ACCEPTED)
//    @GetMapping
//    public SingleResponseDto<String> schedule(@RequestParam(name = "orderId") Long orderId) {
//        Order order = orderService.findOrder(orderId);
//        List<OrderItem> orderItems = order.getOrderItems();
//        applySchedule(order, orderItems);
//
//        return new SingleResponseDto<>(SUCCESS_SHCHDULE);
//    }
//
//    @PatchMapping
//    public SingleResponseDto<SubResponse> changePeriod(
//        @RequestParam(name = "orderId") Long orderId,
//        @RequestParam(name = "period") Integer period,
//        @RequestParam(name = "itemOrderId") Long itemOrderId
//    ) {
//        log.info("스케쥴 변화");
//        OrderItem orderItem = subscriptionService.changePeriod(orderId, period, itemOrderId);
//        return new SingleResponseDto<>(
//            itemOrderMapper.itemOrderToSubResponse(orderItem, itemMapper)
//        );
//    }
//
////    @PatchMapping("/delay")
////    public SingleResponseDto<SubResponse> delay(
////        @RequestParam(name = "orderId") Long orderId,
////        @RequestParam(name = "delay") Integer delay,
////        @RequestParam(name = "itemOrderId") Long itemOrderId
////    ) {
////        ItemOrder itemOrder = subscriptionService.delayDelivery(orderId, delay, itemOrderId);
////        return new SingleResponseDto<>(
////            itemOrderMapper.itemOrderToSubResponse(itemOrder, itemMapper));
////    }
//
//    @DeleteMapping
//    public SingleResponseDto<ZonedDateTime> delete(
//        @RequestParam(name = "orderId") Long orderId,
//        @RequestParam(name = "itemOrderId") Long itemOrderId
//    ) {
//        subscriptionService.cancelScheduler(orderId, itemOrderId);
//        return new SingleResponseDto<>(ZonedDateTime.now());
//    }
//
//    private void applySchedule(final Order order, final List<OrderItem> orderItems) {
//        for (OrderItem orderItem : orderItems) {
//            ZonedDateTime nextDelivery = order.getCreatedAt().plusDays(orderItem.getPeriod());
//            log.info("{}", nextDelivery);
//            OrderItem orderItem1 =
//                orderItemService.updateDeliveryInfo(order.getCreatedAt(), nextDelivery, orderItem);
//            log.info("{}", orderItem1.getItemOrderId());
//            subscriptionService.startSchedule(order, orderItem1);
//        }
//    }
//}
//
//
//
