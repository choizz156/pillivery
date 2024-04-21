package com.team33.modulecore.cart.dto;


import com.team33.modulecore.itemcart.dto.ItemCartDto.Response;
import javax.validation.constraints.Positive;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class CartResponseDto {

    @Positive
    private Long cartId;
    private boolean subscription;
    private Response itemCarts;
    private int totalItems;
    private int totalPrice;
    private int totalDiscountPrice;
    private int expectPrice; // 결제 예상 금액 (totalPrice - totalDiscountPrice)
}
