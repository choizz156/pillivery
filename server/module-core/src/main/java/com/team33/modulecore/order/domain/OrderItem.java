package com.team33.modulecore.order.domain;

import java.time.ZonedDateTime;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.team33.modulecore.common.BaseEntity;
import com.team33.modulecore.item.domain.entity.Item;
import com.team33.modulecore.order.domain.entity.Order;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
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
    private SubscriptionInfo subscriptionInfo;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "item_id")
    private Item item;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id")
    private Order order;

    @Builder
    private OrderItem(int quantity, SubscriptionInfo subscriptionInfo, Item item) {
        this.quantity = quantity;
        this.subscriptionInfo = subscriptionInfo;
        this.item = item;
    }

    // public OrderItem(OrderItem origin) {
    //     this.subscriptionInfo = new SubscriptionInfo(
    //         origin.getSubscriptionInfo().getPeriod(),
    //         origin.getSubscriptionInfo().isSubscription()
    //     );
    //     this.quantity = origin.getQuantity();
    //     this.nextDelivery = origin.getNextDelivery();
    //     this.paymentDay = origin.getPaymentDay();
    //     this.item = origin.getItem();
    //     this.order = origin.getOrder();
    // }

    public static OrderItem create(
        Item item,
        SubscriptionInfo subscriptionInfo,
        int quantity
    ) {
        return OrderItem.builder()
            .item(item)
            .subscriptionInfo(subscriptionInfo)
            .quantity(quantity)
            .build();
    }

    public void addOrder(Order order) {
        this.order = order;
    }

    public void addPeriod(int period){
        this.subscriptionInfo.addPeriod(period);
    }

    public void cancelSubscription(){
        this.subscriptionInfo.cancelSubscription();
    }

    public int getPeriod(){
        return subscriptionInfo.getPeriod();
    }

    public boolean isSubscription(){
        return subscriptionInfo.isSubscription();
    }

    public void changeQuantity(int quantity) {
        this.quantity = quantity;
    }

    public boolean containsItem(Long id) {
        return item.getId().equals(id);
    }

    public void addPaymentDay(ZonedDateTime paymentDay) {
        this.subscriptionInfo.addPaymentDay(paymentDay);
    }

    public void applyNextDelivery() {
        this.subscriptionInfo.applyNextDelivery();
    }

    public Date getNextDelivery() {
        return Date.from(subscriptionInfo.getNextDelivery().toInstant());
    }
}
