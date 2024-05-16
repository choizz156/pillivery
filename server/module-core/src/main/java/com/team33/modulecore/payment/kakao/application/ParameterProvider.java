package com.team33.modulecore.payment.kakao.application;


import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import com.team33.modulecore.order.domain.entity.Order;
import com.team33.modulecore.payment.kakao.dto.PaymentParams;

@Component
public class ParameterProvider extends ParamsConst {

    public MultiValueMap<String, String> getOneTimeReqsParams(Order order) {
        var commonReqsParams = getRequestParams(order);
        commonReqsParams.add(CID, ONE_TIME_CID);
        commonReqsParams.add(APPROVAL_URL, ONE_TIME_APPROVAL_URL + order.getId());

        return commonReqsParams;
    }

    public MultiValueMap<String, String> getSubscriptionReqsParams(Order order) {
        var commonReqsParams = getRequestParams(order);
        commonReqsParams.add(CID, SUBSCRIP_CID);
        commonReqsParams.add(APPROVAL_URL, SUBSCRIPTION_APPROVAL_URI);
        return commonReqsParams;
    }

    public MultiValueMap<String, String> getOneTimeApproveParams(
        String tid,
        String pgToken,
        Long orderId
    ) {
        var commonApproveParams =
            getCommonApproveParams(tid, pgToken, orderId);
        commonApproveParams.add(CID, ONE_TIME_CID);
        return commonApproveParams;
    }

    public MultiValueMap<String, String> getSubscriptionFirstApproveParams(
        String tid,
        String pgToken,
        Long orderId
    ) {
        var commonSubsParams = getCommonApproveParams(tid, pgToken, orderId);
        commonSubsParams.add(CID, SUBSCRIP_CID);

        return commonSubsParams;
    }

    public MultiValueMap<String, String> getSubscriptionApproveParams(Order order) {
        var subsApproveParams = getRequestParams(order);
        subsApproveParams.add(SID, order.getSid());
        subsApproveParams.add(CID, SUBSCRIP_CID);

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

        parameters.add(TID, tid);
        parameters.add(PARTNER_ORDER_ID, String.valueOf(orderId));
        parameters.add(PARTNER_USER_ID, PARTNER_USER_ID);
        parameters.add(PG_TOKEN, pgToken);

        return parameters;
    }

    private MultiValueMap<String, String> getCommonReqsParams(PaymentParams paymentParams) {
        MultiValueMap<String, String> parameters = new LinkedMultiValueMap<>();

        parameters.add(PARTNER_ORDER_ID, String.valueOf(paymentParams.getOrderId()));
        parameters.add(PARTNER_USER_ID, PARTNER);
        parameters.add(ITEM_NAME, paymentParams.getItemName());
        parameters.add(QUANTITY, String.valueOf(paymentParams.getQuantity()));
        parameters.add(TOTAL_AMOUNT, String.valueOf(paymentParams.getTotalAmount()));
        parameters.add(TAX_FREE_AMOUNT, "0");
        parameters.add(CANCEL_URL, CANCEL_URI);
        parameters.add(FAIL_URL, FAIL_URI);

        return parameters;
    }

    private PaymentParams getRequestParamsInfo(Order order) {
        String itemName = getItemName(order);

        return PaymentParams.builder()
            .totalAmount(order.getTotalPrice())
            .quantity(order.getTotalQuantity())
            .itemName(itemName)
            .orderId(order.getId())
            .build();
    }

    private String getItemName(Order order) {
        int itemQuantity = order.getTotalItems();
        String itemName = getTitle(order);

        if (itemQuantity == 1) {
            return itemName;
        }

		return itemName + "그 외" + (itemQuantity - 1) + "개";
    }

    private String getTitle(final Order order) {
        return order.getFirstProductname();
    }
}
