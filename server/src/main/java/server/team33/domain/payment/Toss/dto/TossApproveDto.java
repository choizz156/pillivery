package server.team33.domain.payment.Toss.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Builder
@Getter
public class TossApproveDto {
    private final String paymentKey;
    private final String orderId;
    private final int amount;
}


