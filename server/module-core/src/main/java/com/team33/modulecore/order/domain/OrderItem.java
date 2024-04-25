package com.team33.modulecore.order.domain;

import com.team33.modulecore.common.BaseEntity;
import com.team33.modulecore.item.domain.Item;
import java.time.ZonedDateTime;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "order_item")
@Entity
public class OrderItem extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "order_item_id")
    private Long id;

    @Column(nullable = false)
    private int quantity;

    @Embedded
    private SubscriptionItemInfo subscriptionItemInfo;

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
        this.quantity = quantity;
        this.subscriptionItemInfo = new SubscriptionItemInfo(period, subscription);
        this.item = item;
    }

    public OrderItem(OrderItem origin) {
        this.subscriptionItemInfo = new SubscriptionItemInfo(
            origin.getSubscriptionItemInfo().getPeriod(),
            origin.getSubscriptionItemInfo().isSubscription()
        );
        this.quantity = origin.getQuantity();
        this.nextDelivery = origin.getNextDelivery();
        this.paymentDay = origin.getPaymentDay();
        this.item = origin.getItem();
        this.order = origin.getOrder();
    }

    public static OrderItem create(
        Item item,
        SubscriptionItemInfo subscriptionItemInfo,
        int quantity
    ) {
        return OrderItem.builder()
            .item(item)
            .quantity(quantity)
            .period(subscriptionItemInfo.getPeriod())
            .subscription(subscriptionItemInfo.isSubscription())
            .build();
    }

    public void addOrder(Order order) {
        this.order = order;
    }

    public void addPeriod(int period){
        this.subscriptionItemInfo.addPeriod(period);
    }

    public void cancelSubscription(){
        this.subscriptionItemInfo.cancelSubscription();
    }

    public int getPeriod(){
        return subscriptionItemInfo.getPeriod();
    }

    public boolean isSubscription(){
        return subscriptionItemInfo.isSubscription();
    }

    public void changeQuantity(int quantity) {
        this.quantity = quantity;
    }
}
