package com.team33.modulecore.order.domain;


import com.team33.modulecore.common.BaseEntity;
import com.team33.modulecore.item.domain.entity.Item;
import com.team33.modulecore.user.domain.User;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Transient;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.DynamicUpdate;

@Getter
@DynamicUpdate
@Entity(name = "orders")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Order extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "order_id")
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String phone;

    @Column(nullable = false, name = "subscription")
    private boolean isSubscription;

    @Embedded
    private OrderPrice orderPrice;

    private int totalItems;

    private String sid;

    private String tid;

    @Enumerated(EnumType.STRING)
    private OrderStatus orderStatus = OrderStatus.REQUEST;

    @Transient
    private int totalQuantity;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OrderItem> orderItems = new ArrayList<>();

    public Order(Order origin) {

        this.name = origin.getName();
        this.phone = origin.getPhone();
        this.isSubscription = origin.isSubscription();
        this.totalItems = origin.getTotalItems();
        this.orderPrice = new OrderPrice(
            origin.getOrderPrice().getTotalPrice(),
            origin.getOrderPrice().getTotalDiscountPrice()
        );
        this.user = origin.getUser();
        this.orderItems = origin.getOrderItems();
        this.orderStatus = OrderStatus.SUBSCRIBE;
        this.totalQuantity = origin.getTotalQuantity();
        this.sid = origin.getSid();
        this.tid = origin.getTid();
    }

    @Builder
    private Order(
        String name,
        String phone,
        boolean isSubscription,
        int totalItems,
        User user,
        List<OrderItem> orderItems,
        OrderStatus orderStatus,
        int totalQuantity
    ) {
        this.name = name;
        this.phone = phone;
        this.isSubscription = isSubscription;
        this.totalItems = totalItems;
        this.user = user;
        this.orderItems = orderItems;
        this.orderStatus = orderStatus;
        this.totalQuantity = totalQuantity;
    }

    public static Order create(List<OrderItem> orderItems, boolean subscription, User user) {
        Order order = Order.builder()
            .name(user.getRealName())
            .phone(user.getPhone())
            .isSubscription(subscription)
            .user(user)
            .orderStatus(OrderStatus.REQUEST)
            .orderItems(orderItems)
            .totalItems(orderItems.size())
            .build();
        order.addPrice(order.getOrderItems());
        order.getOrderItems().forEach(orderItem -> orderItem.addOrder(order));
        return order;
    }

    public void addSid(String sid) {
        this.sid = sid;
    }

    public void addTid(String tid) {
        this.tid = tid;
    }

    public String getOrdererCity() {
        return this.user.getCityAtAddress();
    }

    public String getOrdererDetailAddress() {
        return this.user.getDetailAddress();
    }

    public Item getFirstItem() {
        return orderItems.get(0).getItem();
    }

    public void changeOrderStatus(OrderStatus orderStatus) {
        this.orderStatus = orderStatus;
    }

    private void addPrice(List<OrderItem> orderItems) {
        this.orderPrice = new OrderPrice(orderItems);
    }

    private void countQuantity() { // 주문의 담긴 상품의 총량을 구하는 메서드

        if (this.orderItems.isEmpty()) {
            this.totalQuantity = 0;
            return;
        }

        this.totalQuantity =
            this.orderItems.stream()
                .map(OrderItem::getQuantity)
                .reduce(0, Integer::sum);
    }
}
