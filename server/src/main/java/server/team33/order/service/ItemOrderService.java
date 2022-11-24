package server.team33.order.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import server.team33.item.repository.ItemRepository;
import server.team33.order.entity.ItemOrder;
import server.team33.order.reposiroty.ItemOrderRepository;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class ItemOrderService {

    private final ItemOrderRepository itemOrderRepository;
    private final ItemRepository itemRepository;

    public List<ItemOrder> createItemOrder(ItemOrder itemOrder) {
        itemOrderRepository.save(itemOrder);
        List<ItemOrder> itemOrders = new ArrayList<>();
        itemOrders.add(itemOrder);

        return itemOrders;
    }

    public int countTotalPrice(List<ItemOrder> itemOrders) {

        if(itemOrders == null) return 0;

        int totalPrice = 0;

        for(ItemOrder itemOrder : itemOrders) {
            int quantity = itemOrder.getQuantity();
            int price = itemOrder.getItem().getPrice();
            totalPrice += (quantity * price);
        }

        return totalPrice;
    }

    public int countDiscountTotalPrice(List<ItemOrder> itemOrders) {

        if(itemOrders == null) return 0;

        int totalDiscountPrice = 0;

        for(ItemOrder itemOrder : itemOrders) {
            int quantity = itemOrder.getQuantity();
            int price = itemOrder.getItem().getPrice();
            int discountRate = itemOrder.getItem().getDiscountRate();
            totalDiscountPrice += (quantity * price * discountRate/100);
        }

        return totalDiscountPrice;
    }

    public int countQuantity(List<ItemOrder> itemOrders) { // 주문의 담긴 상품의 총량을 구하는 메서드

        if(itemOrders == null) return 0;

        int totalquantity = 0;

        for(ItemOrder itemOrder : itemOrders) {
            int quantity = itemOrder.getQuantity();
            totalquantity += quantity;
        }

        return totalquantity;
    }

    public void minusSales(List<ItemOrder> itemOrders) { // 주문 취소할 경우 아이템 판매량에서 제외

        for(ItemOrder itemOrder : itemOrders) {
            int sales = itemOrder.getQuantity();
            itemOrder.getItem().setSales(itemOrder.getItem().getSales() - sales);
            itemRepository.save(itemOrder.getItem());
        }
    }

    public void plusSales(ItemOrder itemOrder) { // 주문 요청할 경우 아이템 판매량 증가

        int sales = itemOrder.getQuantity();
        itemOrder.getItem().setSales(itemOrder.getItem().getSales() + sales);
        itemRepository.save(itemOrder.getItem());
    }

    public void changePeriod( ItemOrder itemOrder, Integer period ){
        itemOrder.setPeriod(period);
    }

    public void delayDelivery( ItemOrder itemOrder, String delay ){
        itemOrder.setNextDelivery(itemOrder.getNextDelivery().plusDays(Long.parseLong(delay)));
    }

    public void changeDeliveryInfo( ItemOrder itemOrder, ZonedDateTime paymentDay, String nextDelivery ){
        itemOrder.setPaymentDay(paymentDay);
        itemOrder.setNextDelivery(ZonedDateTime.parse(nextDelivery));
    }
}
