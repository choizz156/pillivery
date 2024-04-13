package com.team33.modulecore.domain.cart.entity;


import com.team33.modulecore.domain.audit.BaseEntity;
import com.team33.modulecore.domain.item.entity.Item;
import com.team33.modulecore.domain.order.value.OrderItemInfo;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "item_cart")
@Entity
public class ItemCart extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "item_cart_id")
    private Long itemCartId;

    @Embedded
    private OrderItemInfo orderItemInfo;

    @Column(nullable = false)
    private boolean buyNow;

    @ManyToOne
    @JoinColumn(name = "cart_id")
    private Cart cart;

    @ManyToOne
    @JoinColumn(name = "item_id")
    private Item item;

    @Builder
    public ItemCart(OrderItemInfo orderItemInfo, boolean buyNow, Cart cart, Item item) {
        this.orderItemInfo = orderItemInfo;
        this.buyNow = buyNow;
        this.cart = cart;
        this.item = item;
    }

    public void addCart(Cart cart) {
        this.cart = cart;
        if(!this.cart.getItemCarts().contains(this)) {
            this.cart.getItemCarts().add(this);
        }
    }

    // 장바구니에 같은 상품을 또 담을 경우 수량만 증가
    public void addQuantity(int quantity) {
        this.orderItemInfo.addQuantity(quantity);
    }

    public int getQuantity(){
        return orderItemInfo.getQuantity();
    }
    public int getPeriod(){
        return orderItemInfo.getPeriod();
    }

    public boolean getSubscription(){
        return orderItemInfo.isSubscription();
    }

//    @PrePersist
//    public void prePersist() {
//        this.period = this.period == null ? 0 : this.period;
//    }
}
