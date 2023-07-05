package com.server.modulequartz.subscription.controller;

import java.time.ZonedDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import server.team33.domain.item.mapper.ItemMapper;
import server.team33.domain.order.entity.ItemOrder;
import server.team33.domain.order.entity.Order;
import server.team33.domain.order.mapper.ItemOrderMapper;
import server.team33.domain.order.service.ItemOrderService;
import server.team33.domain.order.service.OrderService;
import com.server.modulequartz.subscription.service.SubscriptionService;
import server.team33.global.response.SingleResponseDto;

@Slf4j
@RequiredArgsConstructor
@RequestMapping("/schedule")
@RestController
public class ScheduleController {

    private final SubscriptionService subscriptionService;
    private final OrderService orderService;
    private final ItemOrderService itemOrderService;
    private final ItemOrderMapper itemOrderMapper;
    private final ItemMapper itemMapper;

    @ResponseStatus(HttpStatus.ACCEPTED)
    @GetMapping
    public void startsKakaoSchedule(@RequestParam(name = "orderId") Long orderId) {

        Order order = orderService.findOrder(orderId);
        List<ItemOrder> itemOrders = order.getItemOrders();

        applySchedule(orderId, order, itemOrders);
    }

    @PatchMapping("/change")
    public SingleResponseDto changePeriod(
        @RequestParam(name = "orderId") Long orderId,
        @RequestParam(name = "period") Integer period,
        @RequestParam(name = "itemOrderId") Long itemOrderId
    )
         {
        ItemOrder itemOrder = subscriptionService.changePeriod(orderId, period, itemOrderId);
        return new SingleResponseDto<>(itemOrderMapper.itemOrderToSubResponse(itemOrder, itemMapper));
    }

    @PatchMapping("/delay")
    public SingleResponseDto delay(@RequestParam(name = "orderId") Long orderId,
        @RequestParam(name = "delay") Integer delay,
        @RequestParam(name = "itemOrderId") Long itemOrderId){
        ItemOrder itemOrder = subscriptionService.delayDelivery(orderId, delay, itemOrderId);
        return new SingleResponseDto<>(itemOrderMapper.itemOrderToSubResponse(itemOrder, itemMapper));
    }

    @DeleteMapping("/cancel")
    public ZonedDateTime delete(@RequestParam(name = "orderId") Long orderId,
        @RequestParam(name = "itemOrderId") Long itemOrderId) {
        subscriptionService.cancelScheduler(orderId, itemOrderId);
        return ZonedDateTime.now();
    }

    private void applySchedule(final Long orderId, final Order order,
        final List<ItemOrder> itemOrders) {
        for (ItemOrder itemOrder : itemOrders) {
            ZonedDateTime nextDelivery = order.getCreatedAt().plusDays(itemOrder.getPeriod());
            ItemOrder itemOrder1 = itemOrderService.updateDeliveryInfo(orderId,
                order.getCreatedAt(), nextDelivery, itemOrder);
            subscriptionService.startSchedule(order, itemOrder1);
        }
    }
}



