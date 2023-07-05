package team33.modulecore.domain.order.entity;

import java.time.ZonedDateTime;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;
import team33.modulecore.domain.audit.BaseEntity;
import team33.modulecore.domain.item.entity.Item;

@Getter
@Setter
@Entity(name = "ITEM_ORDERS")
@NoArgsConstructor
@AllArgsConstructor
public class ItemOrder extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long itemOrderId;

    @Column(nullable = false)
    private Integer quantity;

    @Column
    @ColumnDefault("0")
    private Integer period;

    @Column(nullable = false)
    private boolean subscription;

    @Column(name = "NEXT_DELIVERY")
    private ZonedDateTime nextDelivery;

    @Column(name = "PAYMENT_DAY")
    private ZonedDateTime paymentDay;

    @ManyToOne
    @JoinColumn(name="ITEM_ID")
    private Item item;

    @ManyToOne
    @JoinColumn(name = "ORDER_ID")
    private Order order;

    public ItemOrder( ItemOrder origin){
        this.quantity = origin.getQuantity();
        this.period = origin.getPeriod();
        this.subscription = origin.isSubscription();
        this.nextDelivery = origin.getNextDelivery();
        this.paymentDay = origin.getPaymentDay();
        this.item = origin.getItem();
        this.order = origin.getOrder();
    }

}
