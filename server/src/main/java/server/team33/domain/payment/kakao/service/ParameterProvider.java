package server.team33.domain.payment.kakao.service;

import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import server.team33.domain.order.entity.Order;
import server.team33.domain.payment.kakao.dto.PaymentParams;

@Component
public class ParameterProvider {

    private static final String PARTNER_USER_ID = "pillivery";
    private static final String ONE_TIME_APPROVAL_URL = "ec2-43-201-37-71.ap-northeast-2.compute.amazonaws.com/payments/kakao/approve/";
    private static final String SUBSCRIPTION_APPROVAL_URI = "ec2-43-201-37-71.ap-northeast-2.compute.amazonaws.com/payments/kakao/subs/approve";
    private static final String CANCEL_URI = "ec2-43-201-37-71.ap-northeast-2.compute.amazonaws.com/cancel";
    private static final String FAIL_URI = "ec2-43-201-37-71.ap-northeast-2.compute.amazonaws.com/fail";
    private static final int NAME_INDEX = 0;

    public MultiValueMap<String, String> getOneTimeReqsParams(Order order) {
        var commonReqsParams = getRequestParams(order);
        commonReqsParams.add("cid", "TC0ONETIME");
        commonReqsParams.add("approval_url", ONE_TIME_APPROVAL_URL + order.getOrderId());

        return commonReqsParams;
    }

    public MultiValueMap<String, String> getSubscriptionReqsParams(Order order) {
        var commonReqsParams = getRequestParams(order);
        commonReqsParams.add("cid", "TCSUBSCRIP");
        commonReqsParams.add("approval_url", SUBSCRIPTION_APPROVAL_URI);
        return commonReqsParams;
    }

    public MultiValueMap<String, String> getOneTimeApproveParams(
        String tid,
        String pgToken,
        Long orderId
    ) {
        var commonApproveParams =
            getCommonApproveParams(tid, pgToken, orderId);
        commonApproveParams.add("cid", "TC0ONETIME");
        return commonApproveParams;
    }

    public MultiValueMap<String, String> getSubscriptionFirstApproveParams(
        String tid,
        String pgToken,
        Long orderId
    ) {
        var commonSubsParams = getCommonApproveParams(tid, pgToken, orderId);
        commonSubsParams.add("cid", "TCSUBSCRIP");

        return commonSubsParams;
    }

    public MultiValueMap<String, String> getSubscriptionApproveParams(String sid, Order order) {
        var subsApproveParams = getRequestParams(order);
        subsApproveParams.add("sid", sid);
        subsApproveParams.add("cid", "TCSUBSCRIP");

        return subsApproveParams;
    }

    private MultiValueMap<String, String> getRequestParams(Order order) {
        PaymentParams requestParamsInfo = getRequestParamsInfo(order);
        return getCommonReqsParams(requestParamsInfo);
    }

    private MultiValueMap<String, String> getCommonApproveParams(
        String tid,
        String pgToken,
        Long orderId
    ) {
        MultiValueMap<String, String> parameters = new LinkedMultiValueMap<>();

        parameters.add("tid", tid);
        parameters.add("partner_order_id", String.valueOf(orderId));
        parameters.add("partner_user_id", PARTNER_USER_ID);
        parameters.add("pg_token", pgToken);

        return parameters;
    }

    private MultiValueMap<String, String> getCommonReqsParams(PaymentParams paymentParams) {
        MultiValueMap<String, String> parameters = new LinkedMultiValueMap<>();

        parameters.add("partner_order_id", String.valueOf(paymentParams.getOrderId()));
        parameters.add("partner_user_id", PARTNER_USER_ID);
        parameters.add("item_name", paymentParams.getItemName());
        parameters.add("quantity", String.valueOf(paymentParams.getQuantity()));
        parameters.add("total_amount", String.valueOf(paymentParams.getTotalAmount()));
        parameters.add("tax_free_amount", "0");
        parameters.add("cancel_url", CANCEL_URI);
        parameters.add("fail_url", FAIL_URI);

        return parameters;
    }

    private PaymentParams getRequestParamsInfo(Order order) {
        String itemName = getItemName(order);

        return PaymentParams.builder().totalAmount(order.getTotalPrice())
            .quantity(order.getTotalQuantity())
            .itemName(itemName)
            .orderId(order.getOrderId())
            .build();
    }

    private String getItemName(Order order) {
        Integer itemQuantity = order.getTotalItems();
        String itemName = getTitle(order);

        if (itemQuantity == 1) {
            return itemName;
        }
        return itemName + " 그 외 " + (itemQuantity - 1);
    }

    private String getTitle(final Order order) {
        return order.getItemOrders().get(NAME_INDEX).getItem().getTitle();
    }
}
