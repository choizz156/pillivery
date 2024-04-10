package com.team33.modulecore.domain.order.entity;

import com.team33.modulecore.domain.audit.BaseEntity;
import com.team33.modulecore.domain.item.entity.Item;
import com.team33.modulecore.domain.order.value.OrderItemInfo;
import java.time.ZonedDateTime;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Table(name = "order_item")
@Entity
@NoArgsConstructor
public class OrderItem extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long orderItemId;

    @Embedded
    private OrderItemInfo orderItemInfo;

    private ZonedDateTime nextDelivery;

    private ZonedDateTime paymentDay;

    @ManyToOne
    @JoinColumn(name = "item_id")
    private Item item;

    @ManyToOne
    @JoinColumn(name = "order_id")
    private Order order;

    @Builder
    private OrderItem(int quantity, int period, boolean subscription, Item item) {
        this.orderItemInfo = new OrderItemInfo(quantity, period, subscription);
        this.item = item;
    }

    public OrderItem(OrderItem origin) {
        this.orderItemInfo = new OrderItemInfo(
            origin.getOrderItemInfo().getQuantity(),
            origin.getOrderItemInfo().getPeriod(),
            origin.getOrderItemInfo().isSubscription()
        );
        this.nextDelivery = origin.getNextDelivery();
        this.paymentDay = origin.getPaymentDay();
        this.item = origin.getItem();
        this.order = origin.getOrder();
    }

    public static OrderItem createWithoutOrder(Item item, OrderItemInfo orderItemInfo) {
        return OrderItem.builder()
            .item(item)
            .quantity(orderItemInfo.getQuantity())
            .period(orderItemInfo.getPeriod())
            .subscription(orderItemInfo.isSubscription())
            .build();
    }

    public void addOrder(Order order) {
        this.order = order;
    }

    public void addPeriod(int period){
        this.orderItemInfo.addPeriod(period);
    }

    public void cancelSubscription(){
        this.orderItemInfo.cancelSubscription();
    }

    public int getQuantity(){
        return orderItemInfo.getQuantity();
    }

    public int getPeriod(){
        return orderItemInfo.getPeriod();
    }

    public boolean isSubscription(){
        return orderItemInfo.isSubscription();
    }
}
