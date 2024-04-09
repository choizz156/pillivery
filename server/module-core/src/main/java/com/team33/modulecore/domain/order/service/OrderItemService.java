package com.team33.modulecore.domain.order.service;


import com.team33.modulecore.domain.item.entity.Item;
import com.team33.modulecore.domain.item.repository.ItemRepository;
import com.team33.modulecore.domain.order.dto.OrderDto.Post;
import com.team33.modulecore.domain.order.entity.Order;
import com.team33.modulecore.domain.order.entity.OrderItem;
import com.team33.modulecore.domain.order.repository.OrderItemRepository;
import com.team33.modulecore.domain.order.repository.OrderRepository;
import com.team33.modulecore.global.exception.BusinessLogicException;
import com.team33.modulecore.global.exception.ExceptionCode;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import javax.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@RequiredArgsConstructor
@Transactional
@Service
public class OrderItemService {

    private final OrderItemRepository orderItemRepository;
    private final ItemRepository itemRepository;
    private final OrderRepository orderRepository;

    public List<OrderItem> createOrderItem(Post dto) {
        Item item = getItem(dto);
        OrderItem orderItem = OrderItem.createWithoutOrder(item, dto);
        return getOrderItems(orderItem);
    }

    public OrderItem findItemOrder(long itemOrderId) {
        Optional<OrderItem> optionalItemOrder = orderItemRepository.findById(itemOrderId);
        OrderItem orderItem = optionalItemOrder.orElseThrow(() -> new BusinessLogicException(
            ExceptionCode.ORDER_NOT_FOUND));

        return orderItem;
    }

    public OrderItem changeSubQuantity(long itemOrderId, int upDown) {
        OrderItem orderItem = findItemOrder(itemOrderId);

        orderItem.setQuantity(orderItem.getQuantity() + upDown);
        orderItemRepository.save(orderItem);

        return orderItem;
    }


    public void minusSales(List<OrderItem> orderItems) { // 주문 취소할 경우 아이템 판매량에서 제외

        for (OrderItem orderItem : orderItems) {
            int sales = orderItem.getQuantity();
            orderItem.getItem().setSales(orderItem.getItem().getSales() - sales);
            itemRepository.save(orderItem.getItem());
        }
    }

    public void addSales(OrderItem orderItem) { // 주문 요청할 경우 아이템 판매량 증가
        int sales = orderItem.getQuantity();

//        itemRepository.save(orderItem.getItem());
    }

    public void setItemPeriod(Integer period, OrderItem orderItem) {
        orderItem.setPeriod(period);
        log.error("주기변경 = {}", orderItem.getPeriod());
    }

    public OrderItem delayDelivery(Long orderId, Integer delay, OrderItem io) {

        Optional<Order> order = orderRepository.findById(orderId);

        if (order.isPresent()) {
            OrderItem orderItem = getItemOrder(io, order);
            ZonedDateTime nextDelivery = orderItem.getNextDelivery().plusDays(delay);
            orderItem.setNextDelivery(nextDelivery);
            return orderItem;
        }
        throw new BusinessLogicException(ExceptionCode.ORDER_NOT_FOUND);
    }


    public OrderItem updateDeliveryInfo(
        ZonedDateTime paymentDay,
        ZonedDateTime nextDelivery,
        OrderItem orderItem
    ) {
        orderItem.setPaymentDay(paymentDay);
        orderItem.setNextDelivery(nextDelivery);
        return orderItem;
    }

    public OrderItem itemOrderCopy(Long lastOrderId, Order newOrder, OrderItem io) {
        Optional<Order> orderEntity = orderRepository.findById(lastOrderId);

        if (orderEntity.isPresent()) {
            OrderItem orderItem = new OrderItem(getItemOrder(io, orderEntity));
            orderItem.setOrder(newOrder);
            addSales(orderItem);
            orderItemRepository.save(orderItem);
            return orderItem;
        }
        throw new BusinessLogicException(ExceptionCode.ORDER_NOT_FOUND);
    }

    public void cancelItemOrder(Long orderId, OrderItem orderItem) {
        Optional<Order> order = orderRepository.findById(orderId);
        if (order.isPresent()) {
            OrderItem orderInOrderItem = getItemOrder(orderItem, order);
            orderInOrderItem.setSubscription(false);
            log.warn("is subsucription = {}", orderInOrderItem.isSubscription());
        }
    }

    private List<OrderItem> getOrderItems(OrderItem orderItem) {
        List<OrderItem> orderItems = new ArrayList<>();
        orderItems.add(orderItem);
        return orderItems;
    }

    private Item getItem(Post dto) {
        Optional<Item> optionalItem = itemRepository.findById(dto.getItemId());
        return optionalItem.orElseThrow(
            () -> new BusinessLogicException(ExceptionCode.ITEM_NOT_FOUND)
        );
    }

    private OrderItem getItemOrder(OrderItem io, Optional<Order> order) {
        int i = order.get().getOrderItems().indexOf(io);
        return order.get().getOrderItems().get(i);
    }
}
