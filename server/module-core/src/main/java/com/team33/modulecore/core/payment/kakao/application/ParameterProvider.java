package com.team33.modulecore.core.payment.kakao.application;

import static com.team33.modulecore.core.payment.kakao.application.request.Params.*;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.stereotype.Component;

import com.team33.modulecore.core.order.domain.entity.Order;
import com.team33.modulecore.core.order.domain.entity.SubscriptionOrder;
import com.team33.modulecore.core.payment.kakao.application.refund.RefundContext;
import com.team33.modulecore.core.payment.kakao.application.request.Params;
import com.team33.modulecore.core.payment.kakao.dto.PaymentParams;

@Component
public class ParameterProvider {

	public Map<String, Object> getOneTimePaymentRequestParams(Order order) {

		return addPaymentSpecificParams(
			getRequestParams(order),
			ONE_TIME_CID.getValue(),
			ONE_TIME_APPROVAL_URL.getValue() + "/" + order.getId());
	}

	public Map<String, Object> getSubscriptionPaymentRequestParams(SubscriptionOrder subscriptionOrder) {

		return addPaymentSpecificParams(
			getRequestParams(subscriptionOrder),
			SUBSCRIPTION_CID.getValue(),
			SUBSCRIPTION_APPROVAL_URL.getValue() + "/" + subscriptionOrder.getId());
	}

	public Map<String, Object> getOneTimePaymentApprovalParams(String tid, String pgToken, Long orderId) {

		return addCidParam(getCommonApproveParams(tid, pgToken, orderId), ONE_TIME_CID.getValue());
	}

	public Map<String, Object> getSubscriptionFirstPaymentApprovalParams(String tid, String pgToken, Long orderId) {

		return addCidParam(getCommonApproveParams(tid, pgToken, orderId), SUBSCRIPTION_CID.getValue());
	}

	public Map<String, Object> getSubscriptionPaymentApprovalParams(SubscriptionOrder subscriptionOrder) {

		Map<String, Object> params = getRequestParams(subscriptionOrder);
		params.put(Params.SID.getValue(), subscriptionOrder.getSid());
		return addCidParam(params, SUBSCRIPTION_CID.getValue());
	}


	public Map<String, Object> getPaymentRefundParams(RefundContext refundContext, String tid) {

		Map<String, Object> params = new ConcurrentHashMap<>();
		params.put(CANCEL_AMOUNT.getValue(), refundContext.getCancelAmount());
		params.put(CANCEL_TAX_FREE_AMOUNT.getValue(), refundContext.getCancelTaxFreeAmount());
		params.put(TID.getValue(), tid);
		return addCidParam(params, ONE_TIME_CID.getValue());
	}

	public Map<String, Object> getSubsCancelParams(String sid) {

		Map<String, Object> params = new ConcurrentHashMap<>();
		params.put(Params.SID.getValue(), sid);
		return addCidParam(params, SUBSCRIPTION_CID.getValue());
	}

	public Map<String, Object> getPaymentLookupParams(String tid) {

		Map<String, Object> params = new ConcurrentHashMap<>();
		params.put(TID.getValue(), tid);
		return addCidParam(params, ONE_TIME_CID.getValue());
	}

	private Map<String, Object> addPaymentSpecificParams(Map<String, Object> params, String cid, String approvalUrl) {

		params.put(CID.getValue(), cid);
		params.put(APPROVAL_URL.getValue(), approvalUrl);
		return params;
	}

	private Map<String, Object> addCidParam(Map<String, Object> params, String cid) {

		params.put(CID.getValue(), cid);
		return params;
	}

	private Map<String, Object> getRequestParams(Order order) {

		return getCommonReqsParams(getRequestParamsInfo(order));
	}

	private Map<String, Object> getRequestParams(SubscriptionOrder subscriptionOrder) {

		return getCommonReqsParams(getRequestParamsInfo(subscriptionOrder));
	}

	private PaymentParams getRequestParamsInfo(Order order) {

		return PaymentParams.builder()
			.totalAmount(order.getTotalPrice())
			.quantity(order.getTotalQuantity())
			.itemName(getItemName(order))
			.orderId(order.getId())
			.build();
	}

	private PaymentParams getRequestParamsInfo(SubscriptionOrder subscriptionOrder) {

		return PaymentParams.builder()
			.totalAmount(subscriptionOrder.getTotalPrice())
			.quantity(subscriptionOrder.getTotalQuantity())
			.itemName(getItemName(subscriptionOrder))
			.orderId(subscriptionOrder.getId())
			.build();
	}

	private Map<String, Object> getCommonReqsParams(PaymentParams paymentParams) {

		Map<String, Object> parameters = new ConcurrentHashMap<>();
		parameters.put(PARTNER_ORDER_ID.getValue(), String.valueOf(paymentParams.getOrderId()));
		parameters.put(PARTNER_USER_ID.getValue(), Params.PARTNER.getValue());
		parameters.put(ITEM_NAME.getValue(), paymentParams.getItemName());
		parameters.put(QUANTITY.getValue(), String.valueOf(paymentParams.getQuantity()));
		parameters.put(TOTAL_AMOUNT.getValue(), String.valueOf(paymentParams.getTotalAmount()));
		parameters.put(TAX_FREE_AMOUNT.getValue(), "0");
		parameters.put(CANCEL_URL.getValue(), CANCEL_URL.getValue());
		parameters.put(FAIL_URL.getValue(), FAIL_URL.getValue());
		return parameters;
	}

	private Map<String, Object> getCommonApproveParams(String tid, String pgToken, Long orderId) {

		Map<String, Object> parameters = new ConcurrentHashMap<>();
		parameters.put(TID.getValue(), tid);
		parameters.put(PARTNER_ORDER_ID.getValue(), String.valueOf(orderId));
		parameters.put(PARTNER_USER_ID.getValue(), Params.PARTNER.getValue());
		parameters.put(PG_TOKEN.getValue(), pgToken);
		return parameters;
	}

	private String getItemName(Order order) {

		int itemQuantity = order.getTotalItemsCount();
		String itemName = order.getMainItemName();
		return itemQuantity == 1 ? itemName : itemName + " 그 외 " + (itemQuantity - 1) + "개";
	}

	private String getItemName(SubscriptionOrder subscriptionOrder) {

		return subscriptionOrder.getMainItemName();
	}
}
