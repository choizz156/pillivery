package com.team33.modulecore.domain.order.entity;


import com.team33.modulecore.domain.audit.BaseEntity;
import com.team33.modulecore.domain.order.value.Address;
import com.team33.modulecore.domain.order.value.Price;
import com.team33.modulecore.domain.user.entity.User;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Transient;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@Entity(name = "orders")
@NoArgsConstructor
public class Order extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long orderId;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String phone;

    @Column(nullable = false, name = "subscription")
    private boolean subscription;

    @Embedded
    private Address address;

    private int totalItems; // 주문에 포함된 아이템 종류

    @Embedded
    private Price price;

    private String sid;

    private String tid;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @OneToMany(mappedBy = "order", cascade = CascadeType.PERSIST, orphanRemoval = true)
    private List<OrderItem> orderItems = new ArrayList<>();

    @Enumerated(EnumType.STRING)
    private OrderStatus orderStatus = OrderStatus.ORDER_REQUEST;

    @Transient
    private int totalQuantity;

    public void addSid(String sid) {
        this.sid = sid;
    }

    public void addTid(String tid) {
        this.tid = tid;
    }

    public Order(Order origin) {

        this.name = origin.getName();
        this.address = new Address(origin.getAddress().getCity(),
            origin.getAddress().getDetailAddress());
        this.phone = origin.getPhone();
        this.subscription = origin.isSubscription();
        this.totalItems = origin.getTotalItems();
        this.price = new Price(price.getTotalPrice(), price.getTotalDiscountPrice(),
            price.getExpectPrice());
        this.user = origin.getUser();
        this.orderItems = origin.getOrderItems();
        this.orderStatus = OrderStatus.ORDER_SUBSCRIBE;
        this.totalQuantity = origin.getTotalQuantity();
        this.sid = origin.getSid();
        this.tid = origin.getTid();
    }

    @Builder
    private Order(
        String name,
        Address address,
        String phone,
        boolean subscription,
        int totalItems,
        User user,
        List<OrderItem> orderItems,
        OrderStatus orderStatus,
        int totalQuantity
    ) {
        this.name = name;
        this.address = address;
        this.phone = phone;
        this.subscription = subscription;
        this.totalItems = totalItems;
        this.user = user;
        this.orderItems = orderItems;
        this.orderStatus = orderStatus;
        this.totalQuantity = totalQuantity;
    }

    public static Order create(List<OrderItem> orderItems, boolean subscription, User user) {
        Order order = Order.builder()
            .address(new Address(user.getAddress().getCity(), user.getAddress().getDetailAddress()))
            .name(user.getRealName())
            .phone(user.getPhone())
            .subscription(subscription)
            .user(user)
            .orderItems(orderItems)
            .totalItems(orderItems.size())
            .build();

        order.calculatePrice(order.getOrderItems());
        return order;
    }

    private void calculatePrice(List<OrderItem> orderItems) {
       this.price = new Price(orderItems);
    }


    private void countQuantity() { // 주문의 담긴 상품의 총량을 구하는 메서드

        if (this.orderItems == null) {
            this.totalQuantity = 0;
            return;
        }

        int totalquantity = 0;

        for (OrderItem orderItem : orderItems) {
            int quantity = orderItem.getOrderItemInfo().getQuantity();
            totalquantity += quantity;
        }

       this.totalQuantity = totalquantity;
    }

}
