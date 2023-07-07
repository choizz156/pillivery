package com.team33.modulecore.domain.cart.entity;

import com.team33.modulecore.domain.user.entity.User;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Builder
@Entity
@AllArgsConstructor
@NoArgsConstructor
public class Cart {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long cartId;

    @Column
    @Setter
    private Integer totalItems;

    @Column
    @Setter
    private Integer totalPrice;

    @Column
    @Setter
    private Integer totalDiscountPrice;

    @Column
    @Setter
    private Integer subTotalItems;

    @Column
    @Setter
    private Integer subTotalPrice;

    @Column
    @Setter
    private Integer subTotalDiscountPrice;

    @OneToOne
    private User user;
    @OneToMany(mappedBy = "cart", cascade = CascadeType.PERSIST)
    List<ItemCart> itemCarts = new ArrayList<>();

    public void addItemCart(ItemCart itemCart) {
        this.itemCarts.add(itemCart);
        if(itemCart.getCart() != this) {
            itemCart.addCart(this);
        }
    }
    // 회원 한 명이 하나의 장바구니를 가지므로 회원당 1회만 장바구니 생성
    public static Cart createCart(User user) {
        Cart cart = new Cart();
        cart.user = user;
        user.addCart(cart);
        return cart;
    }
}
