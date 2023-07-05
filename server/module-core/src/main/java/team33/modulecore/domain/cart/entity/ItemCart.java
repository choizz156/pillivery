package team33.modulecore.domain.cart.entity;


import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;
import team33.modulecore.domain.audit.BaseEntity;
import team33.modulecore.domain.item.entity.Item;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity(name = "ITEM_CART")
public class ItemCart extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long itemCartId;

    @Column(nullable = false)
    private Integer quantity;

    @Column
    @ColumnDefault("0")
    private Integer period;

    @Column(nullable = false)
    private boolean buyNow;

    @Column(nullable = false)
    private boolean subscription;

    @ManyToOne
    @JoinColumn(name = "CART_ID")
    private Cart cart;

    @ManyToOne
    @JoinColumn(name = "ITEM_ID")
    private Item item;

    public void addCart(Cart cart) {
        this.cart = cart;
        if(!this.cart.getItemCarts().contains(this)) {
            this.cart.getItemCarts().add(this);
        }
    }

    // 장바구니에 같은 상품을 또 담을 경우 수량만 증가
    public void addQuantity(int quantity) {
        this.quantity += quantity;
    }

//    @PrePersist
//    public void prePersist() {
//        this.period = this.period == null ? 0 : this.period;
//    }
}
