package server.team33.domain.payment.toss.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import server.team33.domain.order.service.OrderService;
import server.team33.domain.payment.toss.service.TossPayService;

@RequiredArgsConstructor
@RequestMapping("/payments")
@RestController
public class TossPayController {

    private final TossPayService tossPayService;
    private final OrderService orderService;

    @GetMapping("/toss/approve")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public String tossApprove(
        @RequestParam("paymentKey") String paymentKey,
        @RequestParam("amount") int amount,
        @RequestParam(name = "orderId") String orderId
    ) {
        String approve = tossPayService.approve(paymentKey, orderId, amount);
        orderId = orderId.replace("abcdef", "");
        orderService.completeOrder(Long.parseLong(orderId));

        return approve;
    }
}
