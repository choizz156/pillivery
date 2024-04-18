package com.team33.modulecore.cart.dto;


import javax.validation.constraints.Positive;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import com.team33.modulecore.itemcart.dto.ItemCartDto.Response;
import com.team33.modulecore.common.dto.MultiResponseDto;

@Getter
@Setter
@NoArgsConstructor
public class CartResponseDto {

    @Positive
    private Long cartId;
    private boolean subscription;
    private MultiResponseDto<Response> itemCarts;
    private int totalItems;
    private int totalPrice;
    private int totalDiscountPrice;
    private int expectPrice; // 결제 예상 금액 (totalPrice - totalDiscountPrice)
}
