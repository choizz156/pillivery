package com.team33.modulecore.cart.domain;

import com.team33.modulecore.itemcart.domain.ItemCart;
import com.team33.modulecore.user.domain.User;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
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

    @Setter
    private Integer totalItems;

    @Setter
    private Integer totalPrice;

    @Setter
    private Integer totalDiscountPrice;
    
    @Setter
    private Integer subTotalItems;

    @Setter
    private Integer subTotalPrice;

    @Setter
    private Integer subTotalDiscountPrice;

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
        user.addCart(cart);
        return cart;
    }

    public void changeSubTotalPrice(int totalPrice) {
        this.subTotalPrice = totalPrice;
    }

    public void changeSubTotalItems(int totalItems) {
        this.subTotalItems = totalItems;
    }

    public void changeTotalPrice(int totalPrice) {
        this.totalPrice = totalPrice;
    }

    public void changeTotalItems(int totalItems) {
        this.totalItems = totalItems;
    }
}
