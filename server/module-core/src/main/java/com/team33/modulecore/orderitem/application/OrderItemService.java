package com.team33.modulecore.orderitem.application;


import com.team33.modulecore.itemcart.domain.ItemCart;
import com.team33.modulecore.itemcart.repository.ItemCartRepository;
import com.team33.modulecore.item.domain.Item;
import com.team33.modulecore.item.repository.ItemRepository;
import com.team33.modulecore.order.domain.Order;
import com.team33.modulecore.orderitem.domain.OrderItem;
import com.team33.modulecore.orderitem.domain.OrderItemInfo;
import com.team33.modulecore.orderitem.repository.OrderItemRepository;
import com.team33.modulecore.order.domain.OrderRepository;
import com.team33.modulecore.exception.BusinessLogicException;
import com.team33.modulecore.exception.ExceptionCode;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@RequiredArgsConstructor
@Transactional
@Service
public class OrderItemService {

    private final ItemCartRepository itemCartRepository;
    private final OrderItemRepository orderItemRepository;
    private final ItemRepository itemRepository;
    private final OrderRepository orderRepository;

    @Transactional(readOnly = true)
    public List<OrderItem> getOrderItemSingle(long itemId, OrderItemInfo orderItemInfo) {
        Item item = getItem(itemId);
        OrderItem orderItem = OrderItem.createWithoutOrder(item, orderItemInfo);
        return getOrderItems(orderItem);
    }

    public List<OrderItem> getOrderItemsInCart(List<ItemCart> itemCarts) {
        List<OrderItem> orderItemList = new ArrayList<>();
        itemCarts.forEach(itemCart -> {
            createOrderItem(itemCart, orderItemList);
            itemCartRepository.deleteById(itemCart.getItemCartId());
        });
        return orderItemList;
    }

    private void createOrderItem(ItemCart itemCart, List<OrderItem> orderItemList) {
        OrderItem orderItem = OrderItem.createWithoutOrder(
            itemCart.getItem(),
            itemCart.getOrderItemInfo()
        );
        orderItemList.add(orderItem);
    }

    public OrderItem findItemOrder(long itemOrderId) {
        Optional<OrderItem> optionalItemOrder = orderItemRepository.findById(itemOrderId);
        OrderItem orderItem = optionalItemOrder.orElseThrow(() -> new BusinessLogicException(
            ExceptionCode.ORDER_NOT_FOUND));

        return orderItem;
    }

//    public OrderItem changeSubQuantity(long itemOrderId, int upDown) {
//        OrderItem orderItem = findItemOrder(itemOrderId);
//
//        orderItem.setQuantity(orderItem.getQuantity() + upDown);
//        orderItemRepository.save(orderItem);
//
//        return orderItem;
//    }


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

    public void setItemPeriod(int period, OrderItem orderItem) {
        orderItem.addPeriod(period);
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
            orderInOrderItem.cancelSubscription();
            log.warn("is subsucription = {}", orderInOrderItem.isSubscription());
        }
    }

    private List<OrderItem> getOrderItems(OrderItem orderItem) {
        List<OrderItem> orderItems = new ArrayList<>();
        orderItems.add(orderItem);
        return orderItems;
    }

    private Item getItem(long id) {
        Optional<Item> optionalItem = itemRepository.findById(id);
        return optionalItem.orElseThrow(
            () -> new BusinessLogicException(ExceptionCode.ITEM_NOT_FOUND)
        );
    }

    private OrderItem getItemOrder(OrderItem io, Optional<Order> order) {
        int i = order.get().getOrderItems().indexOf(io);
        return order.get().getOrderItems().get(i);
    }
}
