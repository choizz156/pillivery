package team33.modulecore.domain.cart.dto;


import javax.validation.constraints.Positive;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import team33.modulecore.domain.cart.dto.ItemCartDto.Response;
import team33.modulecore.global.response.MultiResponseDto;

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
