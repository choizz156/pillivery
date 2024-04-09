package com.team33.modulecore.domain.order.entity;

import com.team33.modulecore.domain.audit.BaseEntity;
import com.team33.modulecore.domain.item.entity.Item;
import com.team33.modulecore.domain.order.dto.OrderDto.Post;
import java.time.ZonedDateTime;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;

@Getter
@Setter
@Entity(name = "order_item")
@NoArgsConstructor
public class OrderItem extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long itemOrderId;

    @Column(nullable = false)
    private int quantity;

    @ColumnDefault("0")
    private int period;

    @Column(nullable = false)
    private boolean subscription;

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
        this.period = period;
        this.subscription = subscription;
        this.item = item;
    }

    public OrderItem(OrderItem origin) {
        this.quantity = origin.getQuantity();
        this.period = origin.getPeriod();
        this.subscription = origin.isSubscription();
        this.nextDelivery = origin.getNextDelivery();
        this.paymentDay = origin.getPaymentDay();
        this.item = origin.getItem();
        this.order = origin.getOrder();
    }

    public static OrderItem createWithoutOrder(Item item, Post dto) {
        return OrderItem.builder()
            .item(item)
            .quantity(dto.getQuantity())
            .period(dto.getPeriod())
            .subscription(dto.isSubscription())
            .build();
    }

    public void addOrder(Order order) {
        this.order = order;
    }

}
