package team33.modulecore.domain.payment.kakao.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
public class PaymentParams {

    private final int totalAmount;
    private final int quantity;
    private final String itemName;
    private final String sid;
    private final Long orderId;


    @Builder
    public PaymentParams(
                            int totalAmount,
                            int quantity,
                            String itemName,
                            String sid,
                            Long orderId
    ) {
        this.totalAmount = totalAmount;
        this.quantity = quantity;
        this.itemName = itemName;
        this.sid = sid;
        this.orderId = orderId;
    }
}
